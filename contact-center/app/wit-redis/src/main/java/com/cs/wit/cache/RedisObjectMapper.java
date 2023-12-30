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

package com.cs.wit.cache;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.core.KotlinDetector;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.util.ClassUtils;

/**
 * RedisObjectMapper
 *
 * @author Dely
 * @version 1.0
 * @date 2023-12 add
 */
public class RedisObjectMapper extends ObjectMapper {
  public RedisObjectMapper() {
    super();

    this.registerModule(new Jdk8Module())
        .registerModule(new JavaTimeModule())
        .registerModule(new ParameterNamesModule());

    GenericJackson2JsonRedisSerializer.registerNullValueSerializer(this, null);
    StdTypeResolverBuilder typer = new TypeResolverBuilder(DefaultTyping.EVERYTHING,
        this.getPolymorphicTypeValidator());
    typer = typer.init(JsonTypeInfo.Id.CLASS, null);
    typer = typer.inclusion(JsonTypeInfo.As.PROPERTY);
    this.setDefaultTyping(typer);
  }

  /**
   * Custom {@link StdTypeResolverBuilder} that considers typing for non-primitive types. Primitives, their wrappers and
   * primitive arrays do not require type hints. The default {@code DefaultTyping#EVERYTHING} typing does not satisfy
   * those requirements.
   *
   * @author Mark Paluch
   * @since 2.7.2
   */
  private static class TypeResolverBuilder extends ObjectMapper.DefaultTypeResolverBuilder {

    public TypeResolverBuilder(DefaultTyping t, PolymorphicTypeValidator ptv) {
      super(t, ptv);
    }

    @Override
    public ObjectMapper.DefaultTypeResolverBuilder withDefaultImpl(Class<?> defaultImpl) {
      return this;
    }

    /**
     * Method called to check if the default type handler should be used for given type. Note: "natural types" (String,
     * Boolean, Integer, Double) will never use typing; that is both due to them being concrete and final, and since
     * actual serializers and deserializers will also ignore any attempts to enforce typing.
     */
    @Override
    public boolean useForType(JavaType t) {

      if (t.isJavaLangObject()) {
        return true;
      }

      t = resolveArrayOrWrapper(t);

      if (t.isEnumType() || ClassUtils.isPrimitiveOrWrapper(t.getRawClass())) {
        return false;
      }

      if (t.isFinal() && !KotlinDetector.isKotlinType(t.getRawClass())
          && t.getRawClass().getPackageName().startsWith("java")) {
        return false;
      }

      // [databind#88] Should not apply to JSON tree models:
      return !TreeNode.class.isAssignableFrom(t.getRawClass());
    }

    private JavaType resolveArrayOrWrapper(JavaType type) {

      while (type.isArrayType()) {
        type = type.getContentType();
        if (type.isReferenceType()) {
          type = resolveArrayOrWrapper(type);
        }
      }

      while (type.isReferenceType()) {
        type = type.getReferencedType();
        if (type.isArrayType()) {
          type = resolveArrayOrWrapper(type);
        }
      }

      return type;
    }
  }
}
