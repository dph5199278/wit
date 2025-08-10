/*
 * Copyright (C) 2023 Dely<https://github.com/dph5199278>, All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cs.wit.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;
import java.util.function.Supplier;
import cn.hutool.v7.crypto.SecureUtil;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.util.Assert;
import org.springframework.web.util.WebUtils;

/**
 * CSRF Token Handler
 * @author Dely
 * @version 1.0
 * @date 2023 -12 add
 */
public final class CryptoCsrfTokenRequestAttributeHandler extends CsrfTokenRequestAttributeHandler {

  /**
   * The constant DEFAULT_CSRF_COOKIE_NAME.
   */
  public static final String DEFAULT_CSRF_COOKIE_NAME = "XSRF-TOKEN";

  private final int AES_BYTES_SIZE = 16;
  private SecureRandom secureRandom = new SecureRandom("X-CSRF-TOKEN-aes".getBytes(StandardCharsets.ISO_8859_1));

  /**
   * Sets secure random.
   *
   * @param secureRandom the secure random
   */
  public void setSecureRandom(SecureRandom secureRandom) {
    Assert.notNull(secureRandom, "secureRandom cannot be null");
    this.secureRandom = secureRandom;
  }

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      Supplier<CsrfToken> deferredCsrfToken) {
    Assert.notNull(request, "request cannot be null");
    Assert.notNull(response, "response cannot be null");
    Assert.notNull(deferredCsrfToken, "deferredCsrfToken cannot be null");
    Supplier<CsrfToken> updatedCsrfToken = deferCsrfTokenUpdate(deferredCsrfToken);
    super.handle(request, response, updatedCsrfToken);

    Optional<String> tokenOp = Optional.ofNullable(request.getAttribute(CsrfToken.class.getName()))
        .map(obj -> (CsrfToken) obj)
        .map(CsrfToken::getToken);
    Optional<String> cookieOp = Optional.ofNullable(WebUtils.getCookie(request, DEFAULT_CSRF_COOKIE_NAME))
        .map(Cookie::getValue);
    if(cookieOp.isEmpty()
    || tokenOp
        .filter(token -> token.equals(cookieOp.get()))
        .isEmpty()) {
      // Token is being added to the XSRF-TOKEN cookie.
      Cookie cookie = new Cookie(DEFAULT_CSRF_COOKIE_NAME, tokenOp.orElse(""));
      cookie.setPath("/");
      cookie.setHttpOnly(true);
      cookie.setAttribute("SameSite", "Lax");
      response.addCookie(cookie);
    }
  }

  private Supplier<CsrfToken> deferCsrfTokenUpdate(Supplier<CsrfToken> csrfTokenSupplier) {
    return new CachedCsrfTokenSupplier(() -> {
      CsrfToken csrfToken = csrfTokenSupplier.get();
      Assert.state(csrfToken != null, "csrfToken supplier returned null");
      String updatedToken = createEncryptCsrfToken(this.secureRandom, csrfToken.getToken());
      return new DefaultCsrfToken(csrfToken.getHeaderName(), csrfToken.getParameterName(), updatedToken);
    });
  }

  @Override
  public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
    String actualToken = resolveCsrfTokenValueByDefaultCookies(request);
    if(null == actualToken) {
      actualToken = super.resolveCsrfTokenValue(request, csrfToken);
    }
    return getTokenValue(actualToken, csrfToken.getToken());
  }

  private String resolveCsrfTokenValueByDefaultCookies(HttpServletRequest request) {
    return Optional.ofNullable(WebUtils.getCookie(request, DEFAULT_CSRF_COOKIE_NAME))
        .map(Cookie::getValue)
        .orElse(null);
  }

  private String getTokenValue(String actualToken, String token) {
    if(null == actualToken) {
      return null;
    }

    byte[] actualBytes;
    try {
      actualBytes = Base64.getUrlDecoder().decode(actualToken);
    }
    catch (Exception ex) {
      return null;
    }

    byte[] tokenBytes = Utf8.encode(token);
    int tokenSize = tokenBytes.length;
    if (actualBytes.length < tokenSize) {
      return null;
    }

    return generateDecryptCsrfToken(this.secureRandom, actualBytes);
  }

  private String generateDecryptCsrfToken(SecureRandom secureRandom, byte[] actualBytes) {
    byte[] randomBytes = new byte[AES_BYTES_SIZE];
    int halfLength = randomBytes.length >> 1;
    System.arraycopy(actualBytes, 0, randomBytes, 0, halfLength);
    System.arraycopy(actualBytes, actualBytes.length - halfLength, randomBytes, halfLength, halfLength);

    byte[] tokenBytes = new byte[actualBytes.length - randomBytes.length];
    System.arraycopy(actualBytes, halfLength, tokenBytes, 0, tokenBytes.length);

    return SecureUtil.aes(randomBytes).decryptStr(tokenBytes);
  }

  private String createEncryptCsrfToken(SecureRandom secureRandom, String token) {
    byte[] randomBytes = new byte[AES_BYTES_SIZE];
    secureRandom.nextBytes(randomBytes);

    byte[] tokenBytes = SecureUtil.aes(randomBytes).encrypt(token);

    byte[] randomBytes1 = new byte[randomBytes.length >> 1];
    System.arraycopy(randomBytes, 0, randomBytes1, 0, randomBytes1.length);
    byte[] randomBytes2 = new byte[randomBytes1.length];
    System.arraycopy(randomBytes, randomBytes1.length, randomBytes2, 0, randomBytes2.length);
    byte[] combinedBytes = new byte[tokenBytes.length + randomBytes.length];
    System.arraycopy(randomBytes1, 0, combinedBytes, 0, randomBytes1.length);
    System.arraycopy(tokenBytes, 0, combinedBytes, randomBytes1.length, tokenBytes.length);
    System.arraycopy(randomBytes2, 0, combinedBytes, randomBytes1.length + tokenBytes.length, randomBytes2.length);

    return Base64.getUrlEncoder().encodeToString(combinedBytes);
  }

  private static final class CachedCsrfTokenSupplier implements Supplier<CsrfToken> {

    private final Supplier<CsrfToken> delegate;

    private CsrfToken csrfToken;

    private CachedCsrfTokenSupplier(Supplier<CsrfToken> delegate) {
      this.delegate = delegate;
    }

    @Override
    public CsrfToken get() {
      if (this.csrfToken == null) {
        this.csrfToken = this.delegate.get();
      }
      return this.csrfToken;
    }

  }

}
