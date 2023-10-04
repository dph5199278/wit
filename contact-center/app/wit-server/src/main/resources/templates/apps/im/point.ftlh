let data = {};
<#if inviteData??>
const FingerprintJS=function(e){"use strict";var n=function(){return n=Object.assign||function(e){for(var n,t=1,r=arguments.length;t<r;t++)for(var o in n=arguments[t])Object.prototype.hasOwnProperty.call(n,o)&&(e[o]=n[o]);return e},n.apply(this,arguments)};function t(e,n,t,r){return new(t||(t=Promise))((function(o,a){function i(e){try{u(r.next(e))}catch(n){a(n)}}function c(e){try{u(r.throw(e))}catch(n){a(n)}}function u(e){var n;e.done?o(e.value):(n=e.value,n instanceof t?n:new t((function(e){e(n)}))).then(i,c)}u((r=r.apply(e,n||[])).next())}))}function r(e,n){var t,r,o,a,i={label:0,sent:function(){if(1&o[0])throw o[1];return o[1]},trys:[],ops:[]};return a={next:c(0),throw:c(1),return:c(2)},"function"==typeof Symbol&&(a[Symbol.iterator]=function(){return this}),a;function c(c){return function(u){return function(c){if(t)throw new TypeError("Generator is already executing.");for(;a&&(a=0,c[0]&&(i=0)),i;)try{if(t=1,r&&(o=2&c[0]?r.return:c[0]?r.throw||((o=r.return)&&o.call(r),0):r.next)&&!(o=o.call(r,c[1])).done)return o;switch(r=0,o&&(c=[2&c[0],o.value]),c[0]){case 0:case 1:o=c;break;case 4:return i.label++,{value:c[1],done:!1};case 5:i.label++,r=c[1],c=[0];continue;case 7:c=i.ops.pop(),i.trys.pop();continue;default:if(!(o=i.trys,(o=o.length>0&&o[o.length-1])||6!==c[0]&&2!==c[0])){i=0;continue}if(3===c[0]&&(!o||c[1]>o[0]&&c[1]<o[3])){i.label=c[1];break}if(6===c[0]&&i.label<o[1]){i.label=o[1],o=c;break}if(o&&i.label<o[2]){i.label=o[2],i.ops.push(c);break}o[2]&&i.ops.pop(),i.trys.pop();continue}c=n.call(e,i)}catch(u){c=[6,u],r=0}finally{t=o=0}if(5&c[0])throw c[1];return{value:c[0]?c[1]:void 0,done:!0}}([c,u])}}}function o(e,n,t){if(t||2===arguments.length)for(var r,o=0,a=n.length;o<a;o++)!r&&o in n||(r||(r=Array.prototype.slice.call(n,0,o)),r[o]=n[o]);return e.concat(r||Array.prototype.slice.call(n))}function a(e,n){return new Promise((function(t){return setTimeout(t,e,n)}))}function i(e){return!!e&&"function"==typeof e.then}function c(e,n){try{var t=e();i(t)?t.then((function(e){return n(!0,e)}),(function(e){return n(!1,e)})):n(!0,t)}catch(r){n(!1,r)}}function u(e,n,o){return void 0===o&&(o=16),t(this,void 0,void 0,(function(){var t,i,c,u;return r(this,(function(r){switch(r.label){case 0:t=Array(e.length),i=Date.now(),c=0,r.label=1;case 1:return c<e.length?(t[c]=n(e[c],c),(u=Date.now())>=i+o?(i=u,[4,a(0)]):[3,3]):[3,4];case 2:r.sent(),r.label=3;case 3:return++c,[3,1];case 4:return[2,t]}}))}))}function l(e){e.then(void 0,(function(){}))}function s(e,n){e=[e[0]>>>16,65535&e[0],e[1]>>>16,65535&e[1]],n=[n[0]>>>16,65535&n[0],n[1]>>>16,65535&n[1]];var t=[0,0,0,0];return t[3]+=e[3]+n[3],t[2]+=t[3]>>>16,t[3]&=65535,t[2]+=e[2]+n[2],t[1]+=t[2]>>>16,t[2]&=65535,t[1]+=e[1]+n[1],t[0]+=t[1]>>>16,t[1]&=65535,t[0]+=e[0]+n[0],t[0]&=65535,[t[0]<<16|t[1],t[2]<<16|t[3]]}function d(e,n){e=[e[0]>>>16,65535&e[0],e[1]>>>16,65535&e[1]],n=[n[0]>>>16,65535&n[0],n[1]>>>16,65535&n[1]];var t=[0,0,0,0];return t[3]+=e[3]*n[3],t[2]+=t[3]>>>16,t[3]&=65535,t[2]+=e[2]*n[3],t[1]+=t[2]>>>16,t[2]&=65535,t[2]+=e[3]*n[2],t[1]+=t[2]>>>16,t[2]&=65535,t[1]+=e[1]*n[3],t[0]+=t[1]>>>16,t[1]&=65535,t[1]+=e[2]*n[2],t[0]+=t[1]>>>16,t[1]&=65535,t[1]+=e[3]*n[1],t[0]+=t[1]>>>16,t[1]&=65535,t[0]+=e[0]*n[3]+e[1]*n[2]+e[2]*n[1]+e[3]*n[0],t[0]&=65535,[t[0]<<16|t[1],t[2]<<16|t[3]]}function m(e,n){return 32===(n%=64)?[e[1],e[0]]:n<32?[e[0]<<n|e[1]>>>32-n,e[1]<<n|e[0]>>>32-n]:(n-=32,[e[1]<<n|e[0]>>>32-n,e[0]<<n|e[1]>>>32-n])}function f(e,n){return 0===(n%=64)?e:n<32?[e[0]<<n|e[1]>>>32-n,e[1]<<n]:[e[1]<<n-32,0]}function h(e,n){return[e[0]^n[0],e[1]^n[1]]}function v(e){return e=h(e,[0,e[0]>>>1]),e=h(e=d(e,[4283543511,3981806797]),[0,e[0]>>>1]),e=h(e=d(e,[3301882366,444984403]),[0,e[0]>>>1])}function p(e,n){n=n||0;var t,r=(e=e||"").length%16,o=e.length-r,a=[0,n],i=[0,n],c=[0,0],u=[0,0],l=[2277735313,289559509],p=[1291169091,658871167];for(t=0;t<o;t+=16)c=[255&e.charCodeAt(t+4)|(255&e.charCodeAt(t+5))<<8|(255&e.charCodeAt(t+6))<<16|(255&e.charCodeAt(t+7))<<24,255&e.charCodeAt(t)|(255&e.charCodeAt(t+1))<<8|(255&e.charCodeAt(t+2))<<16|(255&e.charCodeAt(t+3))<<24],u=[255&e.charCodeAt(t+12)|(255&e.charCodeAt(t+13))<<8|(255&e.charCodeAt(t+14))<<16|(255&e.charCodeAt(t+15))<<24,255&e.charCodeAt(t+8)|(255&e.charCodeAt(t+9))<<8|(255&e.charCodeAt(t+10))<<16|(255&e.charCodeAt(t+11))<<24],c=m(c=d(c,l),31),a=s(a=m(a=h(a,c=d(c,p)),27),i),a=s(d(a,[0,5]),[0,1390208809]),u=m(u=d(u,p),33),i=s(i=m(i=h(i,u=d(u,l)),31),a),i=s(d(i,[0,5]),[0,944331445]);switch(c=[0,0],u=[0,0],r){case 15:u=h(u,f([0,e.charCodeAt(t+14)],48));case 14:u=h(u,f([0,e.charCodeAt(t+13)],40));case 13:u=h(u,f([0,e.charCodeAt(t+12)],32));case 12:u=h(u,f([0,e.charCodeAt(t+11)],24));case 11:u=h(u,f([0,e.charCodeAt(t+10)],16));case 10:u=h(u,f([0,e.charCodeAt(t+9)],8));case 9:u=d(u=h(u,[0,e.charCodeAt(t+8)]),p),i=h(i,u=d(u=m(u,33),l));case 8:c=h(c,f([0,e.charCodeAt(t+7)],56));case 7:c=h(c,f([0,e.charCodeAt(t+6)],48));case 6:c=h(c,f([0,e.charCodeAt(t+5)],40));case 5:c=h(c,f([0,e.charCodeAt(t+4)],32));case 4:c=h(c,f([0,e.charCodeAt(t+3)],24));case 3:c=h(c,f([0,e.charCodeAt(t+2)],16));case 2:c=h(c,f([0,e.charCodeAt(t+1)],8));case 1:c=d(c=h(c,[0,e.charCodeAt(t)]),l),a=h(a,c=d(c=m(c,31),p))}return a=s(a=h(a,[0,e.length]),i=h(i,[0,e.length])),i=s(i,a),a=s(a=v(a),i=v(i)),i=s(i,a),("00000000"+(a[0]>>>0).toString(16)).slice(-8)+("00000000"+(a[1]>>>0).toString(16)).slice(-8)+("00000000"+(i[0]>>>0).toString(16)).slice(-8)+("00000000"+(i[1]>>>0).toString(16)).slice(-8)}function b(e){return parseInt(e)}function y(e){return parseFloat(e)}function g(e,n){return"number"==typeof e&&isNaN(e)?n:e}function w(e){return e.reduce((function(e,n){return e+(n?1:0)}),0)}function L(e,n){if(void 0===n&&(n=1),Math.abs(n)>=1)return Math.round(e/n)*n;var t=1/n;return Math.round(e*t)/t}function k(e){return e&&"object"==typeof e&&"message"in e?e:{message:e}}function V(e){return"function"!=typeof e}function W(e,n,o){var a=Object.keys(e).filter((function(e){return!function(e,n){for(var t=0,r=e.length;t<r;++t)if(e[t]===n)return!0;return!1}(o,e)})),i=u(a,(function(t){return function(e,n){var t=new Promise((function(t){var r=Date.now();c(e.bind(null,n),(function(){for(var e=[],n=0;n<arguments.length;n++)e[n]=arguments[n];var o=Date.now()-r;if(!e[0])return t((function(){return{error:k(e[1]),duration:o}}));var a=e[1];if(V(a))return t((function(){return{value:a,duration:o}}));t((function(){return new Promise((function(e){var n=Date.now();c(a,(function(){for(var t=[],r=0;r<arguments.length;r++)t[r]=arguments[r];var a=o+Date.now()-n;if(!t[0])return e({error:k(t[1]),duration:a});e({value:t[1],duration:a})}))}))}))}))}));return l(t),function(){return t.then((function(e){return e()}))}}(e[t],n)}));return l(i),function(){return t(this,void 0,void 0,(function(){var e,n,t,o;return r(this,(function(r){switch(r.label){case 0:return[4,i];case 1:return[4,u(r.sent(),(function(e){var n=e();return l(n),n}))];case 2:return e=r.sent(),[4,Promise.all(e)];case 3:for(n=r.sent(),t={},o=0;o<a.length;++o)t[a[o]]=n[o];return[2,t]}}))}))}}function Z(){var e=window,n=navigator;return w(["MSCSSMatrix"in e,"msSetImmediate"in e,"msIndexedDB"in e,"msMaxTouchPoints"in n,"msPointerEnabled"in n])>=4}function S(){var e=window,n=navigator;return w(["msWriteProfilerMark"in e,"MSStream"in e,"msLaunchUri"in n,"msSaveBlob"in n])>=3&&!Z()}function C(){var e=window,n=navigator;return w(["webkitPersistentStorage"in n,"webkitTemporaryStorage"in n,0===n.vendor.indexOf("Google"),"webkitResolveLocalFileSystemURL"in e,"BatteryManager"in e,"webkitMediaStream"in e,"webkitSpeechGrammar"in e])>=5}function x(){var e=window,n=navigator;return w(["ApplePayError"in e,"CSSPrimitiveValue"in e,"Counter"in e,0===n.vendor.indexOf("Apple"),"getStorageUpdates"in n,"WebKitMediaKeys"in e])>=4}function F(){var e=window;return w(["safari"in e,!("DeviceMotionEvent"in e),!("ongestureend"in e),!("standalone"in navigator)])>=3}function M(){var e,n,t=window;return w(["buildID"in navigator,"MozAppearance"in(null!==(n=null===(e=document.documentElement)||void 0===e?void 0:e.style)&&void 0!==n?n:{}),"onmozfullscreenchange"in t,"mozInnerScreenX"in t,"CSSMozDocumentRule"in t,"CanvasCaptureMediaStream"in t])>=4}function Y(){var e=document;return e.fullscreenElement||e.msFullscreenElement||e.mozFullScreenElement||e.webkitFullscreenElement||null}function G(){var e=C(),n=M();if(!e&&!n)return!1;var t=window;return w(["onorientationchange"in t,"orientation"in t,e&&!("SharedWorker"in t),n&&/android/i.test(navigator.appVersion)])>=2}function R(e){var n=new Error(e);return n.name=e,n}function X(e,n,o){var i,c,u;return void 0===o&&(o=50),t(this,void 0,void 0,(function(){var t,l;return r(this,(function(r){switch(r.label){case 0:t=document,r.label=1;case 1:return t.body?[3,3]:[4,a(o)];case 2:return r.sent(),[3,1];case 3:l=t.createElement("iframe"),r.label=4;case 4:return r.trys.push([4,,10,11]),[4,new Promise((function(e,r){var o=!1,a=function(){o=!0,e()};l.onload=a,l.onerror=function(e){o=!0,r(e)};var i=l.style;i.setProperty("display","block","important"),i.position="absolute",i.top="0",i.left="0",i.visibility="hidden",n&&"srcdoc"in l?l.srcdoc=n:l.src="about:blank",t.body.appendChild(l);var c=function(){var e,n;o||("complete"===(null===(n=null===(e=l.contentWindow)||void 0===e?void 0:e.document)||void 0===n?void 0:n.readyState)?a():setTimeout(c,10))};c()}))];case 5:r.sent(),r.label=6;case 6:return(null===(c=null===(i=l.contentWindow)||void 0===i?void 0:i.document)||void 0===c?void 0:c.body)?[3,8]:[4,a(o)];case 7:return r.sent(),[3,6];case 8:return[4,e(l,l.contentWindow)];case 9:return[2,r.sent()];case 10:return null===(u=l.parentNode)||void 0===u||u.removeChild(l),[7];case 11:return[2]}}))}))}function A(e){for(var n=function(e){for(var n,t,r="Unexpected syntax '".concat(e,"'"),o=/^\s*([a-z-]*)(.*)$/i.exec(e),a=o[1]||void 0,i={},c=/([.:#][\w-]+|\[.+?\])/gi,u=function(e,n){i[e]=i[e]||[],i[e].push(n)};;){var l=c.exec(o[2]);if(!l)break;var s=l[0];switch(s[0]){case".":u("class",s.slice(1));break;case"#":u("id",s.slice(1));break;case"[":var d=/^\[([\w-]+)([~|^$*]?=("(.*?)"|([\w-]+)))?(\s+[is])?\]$/.exec(s);if(!d)throw new Error(r);u(d[1],null!==(t=null!==(n=d[4])&&void 0!==n?n:d[5])&&void 0!==t?t:"");break;default:throw new Error(r)}}return[a,i]}(e),t=n[0],r=n[1],o=document.createElement(null!=t?t:"div"),a=0,i=Object.keys(r);a<i.length;a++){var c=i[a],u=r[c].join(" ");"style"===c?j(o.style,u):o.setAttribute(c,u)}return o}function j(e,n){for(var t=0,r=n.split(";");t<r.length;t++){var o=r[t],a=/^\s*([\w-]+)\s*:\s*(.+?)(\s*!([\w-]+))?\s*$/.exec(o);if(a){var i=a[1],c=a[2],u=a[4];e.setProperty(i,c,u||"")}}}var I=["monospace","sans-serif","serif"],J=["sans-serif-thin","ARNO PRO","Agency FB","Arabic Typesetting","Arial Unicode MS","AvantGarde Bk BT","BankGothic Md BT","Batang","Bitstream Vera Sans Mono","Calibri","Century","Century Gothic","Clarendon","EUROSTILE","Franklin Gothic","Futura Bk BT","Futura Md BT","GOTHAM","Gill Sans","HELV","Haettenschweiler","Helvetica Neue","Humanst521 BT","Leelawadee","Letter Gothic","Levenim MT","Lucida Bright","Lucida Sans","Menlo","MS Mincho","MS Outlook","MS Reference Specialty","MS UI Gothic","MT Extra","MYRIAD PRO","Marlett","Meiryo UI","Microsoft Uighur","Minion Pro","Monotype Corsiva","PMingLiU","Pristina","SCRIPTINA","Segoe UI Light","Serifa","SimHei","Small Fonts","Staccato222 BT","TRAJAN PRO","Univers CE 55 Medium","Vrinda","ZWAdobeF"];function H(e){return e.toDataURL()}var P,N;function D(){var e=this;return function(){if(void 0===N){var e=function(){var n=z();E(n)?N=setTimeout(e,2500):(P=n,N=void 0)};e()}}(),function(){return t(e,void 0,void 0,(function(){var e;return r(this,(function(n){switch(n.label){case 0:return E(e=z())?P?[2,o([],P,!0)]:Y()?[4,(t=document,(t.exitFullscreen||t.msExitFullscreen||t.mozCancelFullScreen||t.webkitExitFullscreen).call(t))]:[3,2]:[3,2];case 1:n.sent(),e=z(),n.label=2;case 2:return E(e)||(P=e),[2,e]}var t}))}))}}function z(){var e=screen;return[g(y(e.availTop),null),g(y(e.width)-y(e.availWidth)-g(y(e.availLeft),0),null),g(y(e.height)-y(e.availHeight)-g(y(e.availTop),0),null),g(y(e.availLeft),null)]}function E(e){for(var n=0;n<4;++n)if(e[n])return!1;return!0}function T(e){var n;return t(this,void 0,void 0,(function(){var t,o,i,c,u,l,s;return r(this,(function(r){switch(r.label){case 0:for(t=document,o=t.createElement("div"),i=new Array(e.length),c={},B(o),s=0;s<e.length;++s)"DIALOG"===(u=A(e[s])).tagName&&u.show(),B(l=t.createElement("div")),l.appendChild(u),o.appendChild(l),i[s]=u;r.label=1;case 1:return t.body?[3,3]:[4,a(50)];case 2:return r.sent(),[3,1];case 3:t.body.appendChild(o);try{for(s=0;s<e.length;++s)i[s].offsetParent||(c[e[s]]=!0)}finally{null===(n=o.parentNode)||void 0===n||n.removeChild(o)}return[2,c]}}))}))}function B(e){e.style.setProperty("display","block","important")}function _(e){return matchMedia("(inverted-colors: ".concat(e,")")).matches}function O(e){return matchMedia("(forced-colors: ".concat(e,")")).matches}function U(e){return matchMedia("(prefers-contrast: ".concat(e,")")).matches}function Q(e){return matchMedia("(prefers-reduced-motion: ".concat(e,")")).matches}function K(e){return matchMedia("(dynamic-range: ".concat(e,")")).matches}var q=Math,$=function(){return 0};var ee={default:[],apple:[{font:"-apple-system-body"}],serif:[{fontFamily:"serif"}],sans:[{fontFamily:"sans-serif"}],mono:[{fontFamily:"monospace"}],min:[{fontSize:"1px"}],system:[{fontFamily:"system-ui"}]};var ne={fonts:function(){return X((function(e,n){var t=n.document,r=t.body;r.style.fontSize="48px";var o=t.createElement("div"),a={},i={},c=function(e){var n=t.createElement("span"),r=n.style;return r.position="absolute",r.top="0",r.left="0",r.fontFamily=e,n.textContent="mmMwWLliI0O&1",o.appendChild(n),n},u=I.map(c),l=function(){for(var e={},n=function(n){e[n]=I.map((function(e){return function(e,n){return c("'".concat(e,"',").concat(n))}(n,e)}))},t=0,r=J;t<r.length;t++){n(r[t])}return e}();r.appendChild(o);for(var s=0;s<I.length;s++)a[I[s]]=u[s].offsetWidth,i[I[s]]=u[s].offsetHeight;return J.filter((function(e){return n=l[e],I.some((function(e,t){return n[t].offsetWidth!==a[e]||n[t].offsetHeight!==i[e]}));var n}))}))},domBlockers:function(e){var n=(void 0===e?{}:e).debug;return t(this,void 0,void 0,(function(){var e,t,o,a,i;return r(this,(function(r){switch(r.label){case 0:return x()||G()?(c=atob,e={abpIndo:["#Iklan-Melayang","#Kolom-Iklan-728","#SidebarIklan-wrapper",'[title="ALIENBOLA" i]',c("I0JveC1CYW5uZXItYWRz")],abpvn:[".quangcao","#mobileCatfish",c("LmNsb3NlLWFkcw=="),'[id^="bn_bottom_fixed_"]',"#pmadv"],adBlockFinland:[".mainostila",c("LnNwb25zb3JpdA=="),".ylamainos",c("YVtocmVmKj0iL2NsaWNrdGhyZ2guYXNwPyJd"),c("YVtocmVmXj0iaHR0cHM6Ly9hcHAucmVhZHBlYWsuY29tL2FkcyJd")],adBlockPersian:["#navbar_notice_50",".kadr",'TABLE[width="140px"]',"#divAgahi",c("YVtocmVmXj0iaHR0cDovL2cxLnYuZndtcm0ubmV0L2FkLyJd")],adBlockWarningRemoval:["#adblock-honeypot",".adblocker-root",".wp_adblock_detect",c("LmhlYWRlci1ibG9ja2VkLWFk"),c("I2FkX2Jsb2NrZXI=")],adGuardAnnoyances:[".hs-sosyal","#cookieconsentdiv",'div[class^="app_gdpr"]',".as-oil",'[data-cypress="soft-push-notification-modal"]'],adGuardBase:[".BetterJsPopOverlay",c("I2FkXzMwMFgyNTA="),c("I2Jhbm5lcmZsb2F0MjI="),c("I2NhbXBhaWduLWJhbm5lcg=="),c("I0FkLUNvbnRlbnQ=")],adGuardChinese:[c("LlppX2FkX2FfSA=="),c("YVtocmVmKj0iLmh0aGJldDM0LmNvbSJd"),"#widget-quan",c("YVtocmVmKj0iLzg0OTkyMDIwLnh5eiJd"),c("YVtocmVmKj0iLjE5NTZobC5jb20vIl0=")],adGuardFrench:["#pavePub",c("LmFkLWRlc2t0b3AtcmVjdGFuZ2xl"),".mobile_adhesion",".widgetadv",c("LmFkc19iYW4=")],adGuardGerman:['aside[data-portal-id="leaderboard"]'],adGuardJapanese:["#kauli_yad_1",c("YVtocmVmXj0iaHR0cDovL2FkMi50cmFmZmljZ2F0ZS5uZXQvIl0="),c("Ll9wb3BJbl9pbmZpbml0ZV9hZA=="),c("LmFkZ29vZ2xl"),c("Ll9faXNib29zdFJldHVybkFk")],adGuardMobile:[c("YW1wLWF1dG8tYWRz"),c("LmFtcF9hZA=="),'amp-embed[type="24smi"]',"#mgid_iframe1",c("I2FkX2ludmlld19hcmVh")],adGuardRussian:[c("YVtocmVmXj0iaHR0cHM6Ly9hZC5sZXRtZWFkcy5jb20vIl0="),c("LnJlY2xhbWE="),'div[id^="smi2adblock"]',c("ZGl2W2lkXj0iQWRGb3hfYmFubmVyXyJd"),"#psyduckpockeball"],adGuardSocial:[c("YVtocmVmXj0iLy93d3cuc3R1bWJsZXVwb24uY29tL3N1Ym1pdD91cmw9Il0="),c("YVtocmVmXj0iLy90ZWxlZ3JhbS5tZS9zaGFyZS91cmw/Il0="),".etsy-tweet","#inlineShare",".popup-social"],adGuardSpanishPortuguese:["#barraPublicidade","#Publicidade","#publiEspecial","#queTooltip",".cnt-publi"],adGuardTrackingProtection:["#qoo-counter",c("YVtocmVmXj0iaHR0cDovL2NsaWNrLmhvdGxvZy5ydS8iXQ=="),c("YVtocmVmXj0iaHR0cDovL2hpdGNvdW50ZXIucnUvdG9wL3N0YXQucGhwIl0="),c("YVtocmVmXj0iaHR0cDovL3RvcC5tYWlsLnJ1L2p1bXAiXQ=="),"#top100counter"],adGuardTurkish:["#backkapat",c("I3Jla2xhbWk="),c("YVtocmVmXj0iaHR0cDovL2Fkc2Vydi5vbnRlay5jb20udHIvIl0="),c("YVtocmVmXj0iaHR0cDovL2l6bGVuemkuY29tL2NhbXBhaWduLyJd"),c("YVtocmVmXj0iaHR0cDovL3d3dy5pbnN0YWxsYWRzLm5ldC8iXQ==")],bulgarian:[c("dGQjZnJlZW5ldF90YWJsZV9hZHM="),"#ea_intext_div",".lapni-pop-over","#xenium_hot_offers"],easyList:[".yb-floorad",c("LndpZGdldF9wb19hZHNfd2lkZ2V0"),c("LnRyYWZmaWNqdW5reS1hZA=="),".textad_headline",c("LnNwb25zb3JlZC10ZXh0LWxpbmtz")],easyListChina:[c("LmFwcGd1aWRlLXdyYXBbb25jbGljayo9ImJjZWJvcy5jb20iXQ=="),c("LmZyb250cGFnZUFkdk0="),"#taotaole","#aafoot.top_box",".cfa_popup"],easyListCookie:[".ezmob-footer",".cc-CookieWarning","[data-cookie-number]",c("LmF3LWNvb2tpZS1iYW5uZXI="),".sygnal24-gdpr-modal-wrap"],easyListCzechSlovak:["#onlajny-stickers",c("I3Jla2xhbW5pLWJveA=="),c("LnJla2xhbWEtbWVnYWJvYXJk"),".sklik",c("W2lkXj0ic2tsaWtSZWtsYW1hIl0=")],easyListDutch:[c("I2FkdmVydGVudGll"),c("I3ZpcEFkbWFya3RCYW5uZXJCbG9jaw=="),".adstekst",c("YVtocmVmXj0iaHR0cHM6Ly94bHR1YmUubmwvY2xpY2svIl0="),"#semilo-lrectangle"],easyListGermany:["#SSpotIMPopSlider",c("LnNwb25zb3JsaW5rZ3J1ZW4="),c("I3dlcmJ1bmdza3k="),c("I3Jla2xhbWUtcmVjaHRzLW1pdHRl"),c("YVtocmVmXj0iaHR0cHM6Ly9iZDc0Mi5jb20vIl0=")],easyListItaly:[c("LmJveF9hZHZfYW5udW5jaQ=="),".sb-box-pubbliredazionale",c("YVtocmVmXj0iaHR0cDovL2FmZmlsaWF6aW9uaWFkcy5zbmFpLml0LyJd"),c("YVtocmVmXj0iaHR0cHM6Ly9hZHNlcnZlci5odG1sLml0LyJd"),c("YVtocmVmXj0iaHR0cHM6Ly9hZmZpbGlhemlvbmlhZHMuc25haS5pdC8iXQ==")],easyListLithuania:[c("LnJla2xhbW9zX3RhcnBhcw=="),c("LnJla2xhbW9zX251b3JvZG9z"),c("aW1nW2FsdD0iUmVrbGFtaW5pcyBza3lkZWxpcyJd"),c("aW1nW2FsdD0iRGVkaWt1b3RpLmx0IHNlcnZlcmlhaSJd"),c("aW1nW2FsdD0iSG9zdGluZ2FzIFNlcnZlcmlhaS5sdCJd")],estonian:[c("QVtocmVmKj0iaHR0cDovL3BheTRyZXN1bHRzMjQuZXUiXQ==")],fanboyAnnoyances:["#ac-lre-player",".navigate-to-top","#subscribe_popup",".newsletter_holder","#back-top"],fanboyAntiFacebook:[".util-bar-module-firefly-visible"],fanboyEnhancedTrackers:[".open.pushModal","#issuem-leaky-paywall-articles-zero-remaining-nag","#sovrn_container",'div[class$="-hide"][zoompage-fontsize][style="display: block;"]',".BlockNag__Card"],fanboySocial:["#FollowUs","#meteored_share","#social_follow",".article-sharer",".community__social-desc"],frellwitSwedish:[c("YVtocmVmKj0iY2FzaW5vcHJvLnNlIl1bdGFyZ2V0PSJfYmxhbmsiXQ=="),c("YVtocmVmKj0iZG9rdG9yLXNlLm9uZWxpbmsubWUiXQ=="),"article.category-samarbete",c("ZGl2LmhvbGlkQWRz"),"ul.adsmodern"],greekAdBlock:[c("QVtocmVmKj0iYWRtYW4ub3RlbmV0LmdyL2NsaWNrPyJd"),c("QVtocmVmKj0iaHR0cDovL2F4aWFiYW5uZXJzLmV4b2R1cy5nci8iXQ=="),c("QVtocmVmKj0iaHR0cDovL2ludGVyYWN0aXZlLmZvcnRobmV0LmdyL2NsaWNrPyJd"),"DIV.agores300","TABLE.advright"],hungarian:["#cemp_doboz",".optimonk-iframe-container",c("LmFkX19tYWlu"),c("W2NsYXNzKj0iR29vZ2xlQWRzIl0="),"#hirdetesek_box"],iDontCareAboutCookies:['.alert-info[data-block-track*="CookieNotice"]',".ModuleTemplateCookieIndicator",".o--cookies--container","#cookies-policy-sticky","#stickyCookieBar"],icelandicAbp:[c("QVtocmVmXj0iL2ZyYW1ld29yay9yZXNvdXJjZXMvZm9ybXMvYWRzLmFzcHgiXQ==")],latvian:[c("YVtocmVmPSJodHRwOi8vd3d3LnNhbGlkemluaS5sdi8iXVtzdHlsZT0iZGlzcGxheTogYmxvY2s7IHdpZHRoOiAxMjBweDsgaGVpZ2h0OiA0MHB4OyBvdmVyZmxvdzogaGlkZGVuOyBwb3NpdGlvbjogcmVsYXRpdmU7Il0="),c("YVtocmVmPSJodHRwOi8vd3d3LnNhbGlkemluaS5sdi8iXVtzdHlsZT0iZGlzcGxheTogYmxvY2s7IHdpZHRoOiA4OHB4OyBoZWlnaHQ6IDMxcHg7IG92ZXJmbG93OiBoaWRkZW47IHBvc2l0aW9uOiByZWxhdGl2ZTsiXQ==")],listKr:[c("YVtocmVmKj0iLy9hZC5wbGFuYnBsdXMuY28ua3IvIl0="),c("I2xpdmVyZUFkV3JhcHBlcg=="),c("YVtocmVmKj0iLy9hZHYuaW1hZHJlcC5jby5rci8iXQ=="),c("aW5zLmZhc3R2aWV3LWFk"),".revenue_unit_item.dable"],listeAr:[c("LmdlbWluaUxCMUFk"),".right-and-left-sponsers",c("YVtocmVmKj0iLmFmbGFtLmluZm8iXQ=="),c("YVtocmVmKj0iYm9vcmFxLm9yZyJd"),c("YVtocmVmKj0iZHViaXp6bGUuY29tL2FyLz91dG1fc291cmNlPSJd")],listeFr:[c("YVtocmVmXj0iaHR0cDovL3Byb21vLnZhZG9yLmNvbS8iXQ=="),c("I2FkY29udGFpbmVyX3JlY2hlcmNoZQ=="),c("YVtocmVmKj0id2Vib3JhbWEuZnIvZmNnaS1iaW4vIl0="),".site-pub-interstitiel",'div[id^="crt-"][data-criteo-id]'],officialPolish:["#ceneo-placeholder-ceneo-12",c("W2hyZWZePSJodHRwczovL2FmZi5zZW5kaHViLnBsLyJd"),c("YVtocmVmXj0iaHR0cDovL2Fkdm1hbmFnZXIudGVjaGZ1bi5wbC9yZWRpcmVjdC8iXQ=="),c("YVtocmVmXj0iaHR0cDovL3d3dy50cml6ZXIucGwvP3V0bV9zb3VyY2UiXQ=="),c("ZGl2I3NrYXBpZWNfYWQ=")],ro:[c("YVtocmVmXj0iLy9hZmZ0cmsuYWx0ZXgucm8vQ291bnRlci9DbGljayJd"),c("YVtocmVmXj0iaHR0cHM6Ly9ibGFja2ZyaWRheXNhbGVzLnJvL3Ryay9zaG9wLyJd"),c("YVtocmVmXj0iaHR0cHM6Ly9ldmVudC4ycGVyZm9ybWFudC5jb20vZXZlbnRzL2NsaWNrIl0="),c("YVtocmVmXj0iaHR0cHM6Ly9sLnByb2ZpdHNoYXJlLnJvLyJd"),'a[href^="/url/"]'],ruAd:[c("YVtocmVmKj0iLy9mZWJyYXJlLnJ1LyJd"),c("YVtocmVmKj0iLy91dGltZy5ydS8iXQ=="),c("YVtocmVmKj0iOi8vY2hpa2lkaWtpLnJ1Il0="),"#pgeldiz",".yandex-rtb-block"],thaiAds:["a[href*=macau-uta-popup]",c("I2Fkcy1nb29nbGUtbWlkZGxlX3JlY3RhbmdsZS1ncm91cA=="),c("LmFkczMwMHM="),".bumq",".img-kosana"],webAnnoyancesUltralist:["#mod-social-share-2","#social-tools",c("LmN0cGwtZnVsbGJhbm5lcg=="),".zergnet-recommend",".yt.btn-link.btn-md.btn"]},t=Object.keys(e),[4,T((i=[]).concat.apply(i,t.map((function(n){return e[n]}))))]):[2,void 0];case 1:return o=r.sent(),n&&function(e,n){for(var t="DOM blockers debug:\n```",r=0,o=Object.keys(e);r<o.length;r++){var a=o[r];t+="\n".concat(a,":");for(var i=0,c=e[a];i<c.length;i++){var u=c[i];t+="\n  ".concat(n[u]?"🚫":"➡️"," ").concat(u)}}console.log("".concat(t,"\n```"))}(e,o),(a=t.filter((function(n){var t=e[n];return w(t.map((function(e){return o[e]})))>.6*t.length}))).sort(),[2,a]}var c}))}))},fontPreferences:function(){return function(e,n){void 0===n&&(n=4e3);return X((function(t,r){var a=r.document,i=a.body,c=i.style;c.width="".concat(n,"px"),c.webkitTextSizeAdjust=c.textSizeAdjust="none",C()?i.style.zoom="".concat(1/r.devicePixelRatio):x()&&(i.style.zoom="reset");var u=a.createElement("div");return u.textContent=o([],Array(n/20<<0),!0).map((function(){return"word"})).join(" "),i.appendChild(u),e(a,i)}),'<!doctype html><html><head><meta name="viewport" content="width=device-width, initial-scale=1">')}((function(e,n){for(var t={},r={},o=0,a=Object.keys(ee);o<a.length;o++){var i=a[o],c=ee[i],u=c[0],l=void 0===u?{}:u,s=c[1],d=void 0===s?"mmMwWLliI0fiflO&1":s,m=e.createElement("span");m.textContent=d,m.style.whiteSpace="nowrap";for(var f=0,h=Object.keys(l);f<h.length;f++){var v=h[f],p=l[v];void 0!==p&&(m.style[v]=p)}t[i]=m,n.appendChild(e.createElement("br")),n.appendChild(m)}for(var b=0,y=Object.keys(ee);b<y.length;b++){r[i=y[b]]=t[i].getBoundingClientRect().width}return r}))},audio:function(){var e=window,n=e.OfflineAudioContext||e.webkitOfflineAudioContext;if(!n)return-2;if(x()&&!F()&&!function(){var e=window;return w(["DOMRectList"in e,"RTCPeerConnectionIceEvent"in e,"SVGGeometryElement"in e,"ontransitioncancel"in e])>=3}())return-1;var t=new n(1,5e3,44100),r=t.createOscillator();r.type="triangle",r.frequency.value=1e4;var o=t.createDynamicsCompressor();o.threshold.value=-50,o.knee.value=40,o.ratio.value=12,o.attack.value=0,o.release.value=.25,r.connect(o),o.connect(t.destination),r.start(0);var a=function(e){var n=3,t=500,r=500,o=5e3,a=function(){};return[new Promise((function(c,u){var s=!1,d=0,m=0;e.oncomplete=function(e){return c(e.renderedBuffer)};var f=function(){setTimeout((function(){return u(R("timeout"))}),Math.min(r,m+o-Date.now()))},h=function(){try{var r=e.startRendering();switch(i(r)&&l(r),e.state){case"running":m=Date.now(),s&&f();break;case"suspended":document.hidden||d++,s&&d>=n?u(R("suspended")):setTimeout(h,t)}}catch(o){u(o)}};h(),a=function(){s||(s=!0,m>0&&f())}})),a]}(t),c=a[0],u=a[1],s=c.then((function(e){return function(e){for(var n=0,t=0;t<e.length;++t)n+=Math.abs(e[t]);return n}(e.getChannelData(0).subarray(4500))}),(function(e){if("timeout"===e.name||"suspended"===e.name)return-3;throw e}));return l(s),function(){return u(),s}},screenFrame:function(){var e=this,n=D();return function(){return t(e,void 0,void 0,(function(){var e,t;return r(this,(function(r){switch(r.label){case 0:return[4,n()];case 1:return e=r.sent(),[2,[(t=function(e){return null===e?null:L(e,10)})(e[0]),t(e[1]),t(e[2]),t(e[3])]]}}))}))}},osCpu:function(){return navigator.oscpu},languages:function(){var e,n=navigator,t=[],r=n.language||n.userLanguage||n.browserLanguage||n.systemLanguage;if(void 0!==r&&t.push([r]),Array.isArray(n.languages))C()&&w([!("MediaSettingsRange"in(e=window)),"RTCEncodedAudioFrame"in e,""+e.Intl=="[object Intl]",""+e.Reflect=="[object Reflect]"])>=3||t.push(n.languages);else if("string"==typeof n.languages){var o=n.languages;o&&t.push(o.split(","))}return t},colorDepth:function(){return window.screen.colorDepth},deviceMemory:function(){return g(y(navigator.deviceMemory),void 0)},screenResolution:function(){var e=screen,n=function(e){return g(b(e),null)},t=[n(e.width),n(e.height)];return t.sort().reverse(),t},hardwareConcurrency:function(){return g(b(navigator.hardwareConcurrency),void 0)},timezone:function(){var e,n=null===(e=window.Intl)||void 0===e?void 0:e.DateTimeFormat;if(n){var t=(new n).resolvedOptions().timeZone;if(t)return t}var r,o=(r=(new Date).getFullYear(),-Math.max(y(new Date(r,0,1).getTimezoneOffset()),y(new Date(r,6,1).getTimezoneOffset())));return"UTC".concat(o>=0?"+":"").concat(Math.abs(o))},sessionStorage:function(){try{return!!window.sessionStorage}catch(e){return!0}},localStorage:function(){try{return!!window.localStorage}catch(e){return!0}},indexedDB:function(){if(!Z()&&!S())try{return!!window.indexedDB}catch(e){return!0}},openDatabase:function(){return!!window.openDatabase},cpuClass:function(){return navigator.cpuClass},platform:function(){var e=navigator.platform;return"MacIntel"===e&&x()&&!F()?function(){if("iPad"===navigator.platform)return!0;var e=screen,n=e.width/e.height;return w(["MediaSource"in window,!!Element.prototype.webkitRequestFullscreen,n>.65&&n<1.53])>=2}()?"iPad":"iPhone":e},plugins:function(){var e=navigator.plugins;if(e){for(var n=[],t=0;t<e.length;++t){var r=e[t];if(r){for(var o=[],a=0;a<r.length;++a){var i=r[a];o.push({type:i.type,suffixes:i.suffixes})}n.push({name:r.name,description:r.description,mimeTypes:o})}}return n}},canvas:function(){var e,n,t=!1,r=function(){var e=document.createElement("canvas");return e.width=1,e.height=1,[e,e.getContext("2d")]}(),o=r[0],a=r[1];if(function(e,n){return!(!n||!e.toDataURL)}(o,a)){t=function(e){return e.rect(0,0,10,10),e.rect(2,2,6,6),!e.isPointInPath(5,5,"evenodd")}(a),function(e,n){e.width=240,e.height=60,n.textBaseline="alphabetic",n.fillStyle="#f60",n.fillRect(100,1,62,20),n.fillStyle="#069",n.font='11pt "Times New Roman"';var t="Cwm fjordbank gly ".concat(String.fromCharCode(55357,56835));n.fillText(t,2,15),n.fillStyle="rgba(102, 204, 0, 0.2)",n.font="18pt Arial",n.fillText(t,4,45)}(o,a);var i=H(o);i!==H(o)?e=n="unstable":(n=i,function(e,n){e.width=122,e.height=110,n.globalCompositeOperation="multiply";for(var t=0,r=[["#f2f",40,40],["#2ff",80,40],["#ff2",60,80]];t<r.length;t++){var o=r[t],a=o[0],i=o[1],c=o[2];n.fillStyle=a,n.beginPath(),n.arc(i,c,40,0,2*Math.PI,!0),n.closePath(),n.fill()}n.fillStyle="#f9c",n.arc(60,60,60,0,2*Math.PI,!0),n.arc(60,60,20,0,2*Math.PI,!0),n.fill("evenodd")}(o,a),e=H(o))}else e=n="";return{winding:t,geometry:e,text:n}},touchSupport:function(){var e,n=navigator,t=0;void 0!==n.maxTouchPoints?t=b(n.maxTouchPoints):void 0!==n.msMaxTouchPoints&&(t=n.msMaxTouchPoints);try{document.createEvent("TouchEvent"),e=!0}catch(r){e=!1}return{maxTouchPoints:t,touchEvent:e,touchStart:"ontouchstart"in window}},vendor:function(){return navigator.vendor||""},vendorFlavors:function(){for(var e=[],n=0,t=["chrome","safari","__crWeb","__gCrWeb","yandex","__yb","__ybro","__firefox__","__edgeTrackingPreventionStatistics","webkit","oprt","samsungAr","ucweb","UCShellJava","puffinDevice"];n<t.length;n++){var r=t[n],o=window[r];o&&"object"==typeof o&&e.push(r)}return e.sort()},cookiesEnabled:function(){var e=document;try{e.cookie="cookietest=1; SameSite=Strict;";var n=-1!==e.cookie.indexOf("cookietest=");return e.cookie="cookietest=1; SameSite=Strict; expires=Thu, 01-Jan-1970 00:00:01 GMT",n}catch(t){return!1}},colorGamut:function(){for(var e=0,n=["rec2020","p3","srgb"];e<n.length;e++){var t=n[e];if(matchMedia("(color-gamut: ".concat(t,")")).matches)return t}},invertedColors:function(){return!!_("inverted")||!_("none")&&void 0},forcedColors:function(){return!!O("active")||!O("none")&&void 0},monochrome:function(){if(matchMedia("(min-monochrome: 0)").matches){for(var e=0;e<=100;++e)if(matchMedia("(max-monochrome: ".concat(e,")")).matches)return e;throw new Error("Too high value")}},contrast:function(){return U("no-preference")?0:U("high")||U("more")?1:U("low")||U("less")?-1:U("forced")?10:void 0},reducedMotion:function(){return!!Q("reduce")||!Q("no-preference")&&void 0},hdr:function(){return!!K("high")||!K("standard")&&void 0},math:function(){var e,n=q.acos||$,t=q.acosh||$,r=q.asin||$,o=q.asinh||$,a=q.atanh||$,i=q.atan||$,c=q.sin||$,u=q.sinh||$,l=q.cos||$,s=q.cosh||$,d=q.tan||$,m=q.tanh||$,f=q.exp||$,h=q.expm1||$,v=q.log1p||$;return{acos:n(.12312423423423424),acosh:t(1e308),acoshPf:(e=1e154,q.log(e+q.sqrt(e*e-1))),asin:r(.12312423423423424),asinh:o(1),asinhPf:function(e){return q.log(e+q.sqrt(e*e+1))}(1),atanh:a(.5),atanhPf:function(e){return q.log((1+e)/(1-e))/2}(.5),atan:i(.5),sin:c(-1e300),sinh:u(1),sinhPf:function(e){return q.exp(e)-1/q.exp(e)/2}(1),cos:l(10.000000000123),cosh:s(1),coshPf:function(e){return(q.exp(e)+1/q.exp(e))/2}(1),tan:d(-1e300),tanh:m(1),tanhPf:function(e){return(q.exp(2*e)-1)/(q.exp(2*e)+1)}(1),exp:f(1),expm1:h(1),expm1Pf:function(e){return q.exp(e)-1}(1),log1p:v(10),log1pPf:function(e){return q.log(1+e)}(10),powPI:function(e){return q.pow(q.PI,e)}(-100)}},videoCard:function(){var e,n=document.createElement("canvas"),t=null!==(e=n.getContext("webgl"))&&void 0!==e?e:n.getContext("experimental-webgl");if(t&&"getExtension"in t){var r=t.getExtension("WEBGL_debug_renderer_info");if(r)return{vendor:(t.getParameter(r.UNMASKED_VENDOR_WEBGL)||"").toString(),renderer:(t.getParameter(r.UNMASKED_RENDERER_WEBGL)||"").toString()}}},pdfViewerEnabled:function(){return navigator.pdfViewerEnabled},architecture:function(){var e=new Float32Array(1),n=new Uint8Array(e.buffer);return e[0]=1/0,e[0]=e[0]-e[0],n[3]}};function te(e){var n=function(e){if(G())return.4;if(x())return F()?.5:.3;var n=e.platform.value||"";if(/^Win/.test(n))return.6;if(/^Mac/.test(n))return.5;return.7}(e),t=function(e){return L(.99+.01*e,1e-4)}(n);return{score:n,comment:"$ if upgrade to Pro: https://fpjs.dev/pro".replace(/\$/g,"".concat(t))}}function re(e){return JSON.stringify(e,(function(e,t){return t instanceof Error?n({name:(r=t).name,message:r.message,stack:null===(o=r.stack)||void 0===o?void 0:o.split("\n")},r):t;var r,o}),2)}function oe(e){return p(function(e){for(var n="",t=0,r=Object.keys(e).sort();t<r.length;t++){var o=r[t],a=e[o],i=a.error?"error":JSON.stringify(a.value);n+="".concat(n?"|":"").concat(o.replace(/([:|\\])/g,"\\$1"),":").concat(i)}return n}(e))}function ae(e){return void 0===e&&(e=50),function(e,n){void 0===n&&(n=1/0);var t=window.requestIdleCallback;return t?new Promise((function(e){return t.call(window,(function(){return e()}),{timeout:n})})):a(Math.min(e,n))}(e,2*e)}function ie(e,n){var o=Date.now();return{get:function(a){return t(this,void 0,void 0,(function(){var t,i,c;return r(this,(function(r){switch(r.label){case 0:return t=Date.now(),[4,e()];case 1:return i=r.sent(),c=function(e){var n;return{get visitorId(){return void 0===n&&(n=oe(this.components)),n},set visitorId(e){n=e},confidence:te(e),components:e,version:"3.4.2"}}(i),(n||(null==a?void 0:a.debug))&&console.log("Copy the text below to get the debug data:\n\n```\nversion: ".concat(c.version,"\nuserAgent: ").concat(navigator.userAgent,"\ntimeBetweenLoadAndGet: ").concat(t-o,"\nvisitorId: ").concat(c.visitorId,"\ncomponents: ").concat(re(i),"\n```")),[2,c]}}))}))}}}function ce(e){var n=void 0===e?{}:e,o=n.delayFallback,a=n.debug;return n.monitoring,t(this,void 0,void 0,(function(){return r(this,(function(e){switch(e.label){case 0:return[4,ae(o)];case 1:return e.sent(),[2,ie(W(ne,{debug:a},[]),a)]}}))}))}var ue={load:ce,hashComponents:oe,componentsToDebugString:re},le=p;return e.componentsToDebugString=re,e.default=ue,e.getFullscreenElement=Y,e.getScreenFrame=D,e.hashComponents=oe,e.isAndroid=G,e.isChromium=C,e.isDesktopSafari=F,e.isEdgeHTML=S,e.isGecko=M,e.isTrident=Z,e.isWebKit=x,e.load=ce,e.loadSources=W,e.murmurX64Hash128=le,e.prepareForSources=ae,e.sources=ne,e.transformSource=function(e,n){var t=function(e){return V(e)?n(e):function(){var t=e();return i(t)?t.then(n):n(t)}};return function(n){var r=e(n);return i(r)?r.then(t):t(r)}},e.withIframe=X,Object.defineProperty(e,"__esModule",{value:!0}),e}({});

let cskefuOnlineUserId;

function ajax(options) {
options = options || {};

options.type = (options.type || "GET").toUpperCase();

options.dataType = options.dataType || 'json';

options.async = options.async || true;

var params = getParams(options.data);
var xhr;

if (window.XMLHttpRequest){

xhr = new XMLHttpRequest();

}else{

xhr = new ActiveXObject('Microsoft.XMLHTTP')
}
xhr.onreadystatechange = function () {
if (xhr.readyState == 4){
var status = xhr.status;
if (status >= 200 && status < 300 ){
options.success && options.success(xhr.responseText,xhr.responseXML);
}else{
options.fail && options.fail(status);
}
}
};
if (options.type == 'GET'){
xhr.open("GET",options.url + '?' + params ,options.async);
xhr.send(null)
}else if (options.type == 'POST'){

xhr.open('POST',options.url,options.async);

xhr.setRequestHeader('Content-Type','application/x-www-form-urlencoded');


xhr.send(params);
}
}

function getParams(data) {
var arr = [];
for (var param in data){
arr.push(encodeURIComponent(param) + '=' +encodeURIComponent(data[param]));
}
arr.push(('randomNumber=' + Math.random()).replace('.'));
return arr.join('&');
}

var protocol = "${schema!''}"

document.cookie=""

function chatoperaInit(info) {
	ajax({
	  url:
		protocol + "://${hostname!''}<#if port?? && port != 80>:${port!''}</#if>/im/chatoperainit?sessionid=${sessionid!''}",
	  type: "POST",
	  data: {
		<#if inviteData.whitelist_mode == true >
			whitelist_mode: ${inviteData.whitelist_mode},
		<#else>
			whitelist_mode: false,
		</#if>
		userid: cskefuOnlineUserId,
		uid: info.uid,
		username: info.username,
		cid: info.cid,
		company_name: info.company_name,
		sid: info.sid,
		system_name: info.system_name
	  },
	  dataType: "json",
	  contentType: "application/json",
	  async: false,
	  success: function(res) {
			<#if inviteData.whitelist_mode == true >
				if(res == 'usc'){
					document.getElementById("ukefu-point").style.display = "block" ;
				}
			</#if>
	  }
	});
  }
  

<#assign style = 'text-align:center;height: 150px;border-radius: 30px;width: 40px;margin: 0px 5px 0 0; float:left ; word-wrap: break-word;overflow: hidden;font-size:15px;color:#FFFFFF;text-align: center;padding-top: 15px;border: 1px solid #DCDCDC;background-color:#008df3;'>
<#if inviteData.consult_vsitorbtn_model?? && inviteData.consult_vsitorbtn_model == "2">
	<#assign style = 'height: 70px;border-radius: 3px; width: 70px;margin: 40px auto;word-wrap:break-word;overflow:hidden;font-size: 22px;color:#FFFFFF;text-align:center;padding-top:10px;border: 1px solid #DCDCDC;background-color:#dddddd;'>
<#elseif inviteData.consult_vsitorbtn_model?? && inviteData.consult_vsitorbtn_model == "3">
	<#assign style = 'height: 70px;border-radius: 70px; width: 70px;margin: 40px auto;word-wrap:break-word;overflow:hidden;font-size: 22px;color:#FFFFFF;text-align:center;padding-top:5px;border: 1px solid #DCDCDC;background-color:#dddddd;'>
</#if>

<#assign text = 'width:25px;'>
<#if inviteData.consult_vsitorbtn_model?? && inviteData.consult_vsitorbtn_model == "2">
	<#assign text = 'width:100%;'>
<#elseif inviteData.consult_vsitorbtn_model?? && inviteData.consult_vsitorbtn_model == "3">
	<#assign text = 'width:100%;'>
</#if>

<#assign theme = 'background-color: #377FED !important;border-color: #377FED !important;'>
<#if inviteData.consult_vsitorbtn_color?? && inviteData.consult_vsitorbtn_color == "2">
	<#assign theme = 'background-color: #67CAFF !important;'>
<#elseif inviteData.consult_vsitorbtn_color?? && inviteData.consult_vsitorbtn_color == "3">
	<#assign theme = 'background-color: #8E8E8E !important;'>
<#elseif inviteData.consult_vsitorbtn_color?? && inviteData.consult_vsitorbtn_color == "4">
	<#assign theme = 'background-color: #32c24d !important;'>
<#elseif inviteData.consult_vsitorbtn_color?? && inviteData.consult_vsitorbtn_color == "5">
	<#assign theme = 'background-color: #E45DB3 !important;'>
<#elseif inviteData.consult_vsitorbtn_color?? && inviteData.consult_vsitorbtn_color == "6">
	<#assign theme = 'background-color: #FF626F !important;'>
</#if>

<#assign position = "right:10px;top:40%;">
<#if inviteData.consult_vsitorbtn_position?? && inviteData.consult_vsitorbtn_position == "right,top">
	<#assign position = "right:10px;top:10px;">
<#elseif inviteData.consult_vsitorbtn_position?? && inviteData.consult_vsitorbtn_position == "right,bottom">
	<#assign position = "right:10px;bottom:10px;">
<#elseif inviteData.consult_vsitorbtn_position?? && inviteData.consult_vsitorbtn_position == "right,middle">
	<#assign position = "right:10px;top:40%;">
<#elseif inviteData.consult_vsitorbtn_position?? && inviteData.consult_vsitorbtn_position == "left,top">
	<#assign position = "left:10px;top:10px;">
<#elseif inviteData.consult_vsitorbtn_position?? && inviteData.consult_vsitorbtn_position == "left,middle">
	<#assign position = "left:10px;top:40%;">
<#elseif inviteData.consult_vsitorbtn_position?? && inviteData.consult_vsitorbtn_position == "left,bottom">
	<#assign position = "left:10px;bottom:10px;">
</#if>

<#assign invitetheme = 'background-color: #377FED !important;'>
<#if inviteData.consult_invite_color?? && inviteData.consult_invite_color == "2">
	<#assign invitetheme = 'background-color: #67CAFF !important;'>
<#elseif inviteData.consult_invite_color?? && inviteData.consult_invite_color == "3">
	<#assign invitetheme = 'background-color: #8E8E8E !important;'>
<#elseif inviteData.consult_invite_color?? && inviteData.consult_invite_color == "4">
	<#assign invitetheme = 'background-color: #32c24d !important;'>
<#elseif inviteData.consult_invite_color?? && inviteData.consult_invite_color == "5">
	<#assign invitetheme = 'background-color: #E45DB3 !important;'>
<#elseif inviteData.consult_invite_color?? && inviteData.consult_invite_color == "6">
	<#assign invitetheme = 'background-color: #FF626F !important;'>
</#if>
var signal = 0 ;
function append(parent, text) {
    if (typeof text === 'string') {
      var temp = document.createElement('div');
      temp.innerHTML = text;
      parent.appendChild(temp);
    }
    else {
      parent.appendChild(text);
    }
}
function closerefresh(){
location.reload();
}

var cskefu = {
	service: {agentno: null},
	time : new Date().getTime(),
	in: protocol + "://${hostname!''}<#if port?? && port != 80>:${port!''}</#if>/im/${appid!''}/userlist?appid=${appid!''}<#if aiid??>&aiid=${aiid}</#if>&orgi=${orgi!''}&client=${client}" ,
	url: protocol + "://${hostname!''}<#if port?? && port != 80>:${port!''}</#if>/im/online?appid=${appid!''}&orgi=${orgi!''}<#if aiid??>&aiid=${aiid}</#if>&client=${client}" ,
	chat: protocol + "://${hostname!''}<#if port?? && port != 80>:${port!''}</#if>/im/index?&appid=${appid!''}<#if aiid??>&aiid=${aiid}</#if>&orgi=${orgi!''}&client=${client}" ,
	refuse: protocol + "://${hostname!''}<#if port?? && port != 80>:${port!''}</#if>/im/refuse?appid=${appid!''}<#if aiid??>&aiid=${aiid}</#if>&orgi=${orgi!''}&client=${client}",
	config: function(d){
		data = d ;
		if(d.id != null && d.id != ''){
		cskefuOnlineUserId = d.id ;
		}
	} ,
	ajax:function(url , success , error) {
		var xhr = false;
		signal = signal + 1 ;
        try {
            xhr = new ActiveXObject("Msxml2.XMLHTTP"); // ie msxml3.0+（IE7.0及以上）
        } catch (e) {
        	try{
          		xhr = new ActiveXObject("Microsoft.XMLHTTP"); //ie msxml2.6（IE5/6）
          	}catch (e2) {
           		xhr = false;
        	}
        }
        if (!xhr && typeof XMLHttpRequest != 'undefined') {// Firefox, Opera 8.0+, Safari
            xhr = new XMLHttpRequest();
        }
		xhr.onreadystatechange = function (data) {
			if (xhr.readyState == 4) {
				var status = xhr.status;
				if (status >= 200 && status < 300) {
					var event = xhr.responseText;
					if(event && event.indexOf('invite') >= 0){
						var agentno = event.substring(event.lastIndexOf(":") + 1).trim();
						cskefu.service.agentno = agentno;
						cskefu.writeinvite();
					}else if(event && event.indexOf('refuse') >= 0){
						cskefu.refuseInvite() ;
					}else if(event && event.indexOf('accept') >= 0){
						cskefu.acceptInvite();
					}
					if(success){
						success(event);
					}
				} else {
					//
					if(error){
						error();
					}
				}
				xhr.abort();
				signal = signal - 1 ;
			}
		}
		xhr.open("GET", url , true);

		try{
			xhr.send(null);
		}catch(e){}
	},
	ping : function(){
		var url = cskefu.url ;
		if(data){
			if(data.id){
				url = url + "&id="+data.id ;
			}
			if(data.name){
				url = url + "&name="+encodeURIComponent(data.name);
			}
			if(data.email){
				url = url + "&email="+encodeURIComponent(data.email);
			}
			if(data.phone){
				url = url + "&phone="+encodeURIComponent(data.phone);
			}
			url = url + "&title="+encodeURIComponent(document.title);
			<#if traceid??>
			url = url+"&traceid=${traceid}"
			</#if>
			url = url+"&url="+encodeURIComponent(location.href)
		}
		cskefu.ajax(url + "&userid="+cskefuOnlineUserId+"&sessionid=${sessionid!''}&sign="+cskefuOnlineUserId+"&t="+new Date().getTime() , function(){cskefu.ping();});
	},
	writepoint:function(){

		var skillHtml = "<#if inviteData?? && inviteData.skill == true && inviteData.consult_skill_fixed == false><div id='ichatContent' class='ichat-content ichat-float-left' style='width:152px;display: block;font-size:14px;;${theme!''};box-shadow: 0 0 10px #DDDDDD;border: 1px solid #DDDDDD;overflow: hidden;text-align: left;'><div>" +
						"<div class='ichat-title ichat-cursor' style='color: #FFFFFF;font-weight:400;font-size: 14px;font-weight: 400;height: 28px;line-height: 28px;overflow: hidden;padding: 0 5px;background-repeat: repeat-x;'><img src='${schema!'http'}://${hostname!''}<#if port?? && port != 80>:${port!''}</#if>/images/logo-icon.png' style='height:20px;vertical-align: middle;'/> <#if inviteData.consult_skill_title??>${inviteData.consult_skill_title!''}</#if></div>"+
						"<div id='ichatItem0' class='ichat-list' style='background: none repeat scroll 0 0 #FFFFFF;overflow-x: hidden;overflow-y: auto;padding: 5px 0;margin: 0;'>"+
						<#if pointAd??>
							<#if pointAd.adtype =="image">
							"<a href='${pointAd.url!''}' title='${pointAd.tiptext!''}' target='_blank'><img src='${schema!'http'}://${hostname!''}<#if port?? && port != 80>:${port!''}</#if>${pointAd.imgurl!''}' style='max-width:140px;max-height:90px;margin:0 5px;vertical-align: middle;'/></a>"+
							<#else>
							"<div style='padding:0px 5px 10px 5px;border-bottom:1px solid #dedede;'><a href='${pointAd.url!''}' title='${pointAd.tiptext!''}' target='_blank' id='point_ad_text'>${((pointAd.content!'')?replace('\r\n','<br>'))?no_esc}</a></div>"+
							</#if>
						<#else>
						"<img src='${schema!'http'}://${hostname!''}<#if port?? && port != 80>:${port!''}</#if>/images/title.jpg' style='width:140px;margin:0 5px;vertical-align: middle;'/>"+
						</#if>
						"<div style='padding:10px 5px;text-align:center;color:#555;'><#if inviteData?? && inviteData.skill == true && inviteData.consult_skill_fixed == false && inviteData.consult_skill_msg??>${(inviteData.consult_skill_msg!'')?no_esc}</#if></div>"+

						<#if skillGroups?? && skillGroups?size gt 0>
						<#list skillGroups as skill>
						<#if inviteData.consult_skill_numbers == 0 || skill_index lt inviteData.consult_skill_numbers >
							<#assign skillNum = 0 >
							<#list agentList as agent>
								<#if agent.organ?? && agent.organ == skill.id>
								<#assign skillNum = skillNum+1 >
								</#if>
							</#list>
							"<div style='padding:5px;color:#555;border:1px solid #dddddd;background-color:#efefef;'>"+
							"<a href='${schema!'http'}://${hostname!''}<#if port?? && port != 80>:${port!''}</#if>/im/text/${appid!''}?skill=${skill.id!''}&orgi=${orgi!''}' onclick='return openAgentChatDialog(this.href)' style='color: #777;    text-decoration: none;'>${skill.name!''}（${skillNum}）</a>"+
							"</div>"+
							<#if inviteData.consult_skill_agent?? && inviteData.consult_skill_agent == true>
							"<ul class='ichat-list-ul' style='border: 0 none;list-style: none outside none;margin: 0;padding: 0;width: 150px;'>"+
							<#if agentList??>
							<#assign agentNum = 0>
							<#list agentList as agent>
								<#if agent.organ?? && agent.organ == skill.id && agentNum lt inviteData.consult_skill_maxagent >
								<#assign agentNum = agentNum + 1>
								"<li style='height: 23px;list-style: none outside none;margin-left: 6px;overflow: hidden;padding: 2px;'>"+
								"<a href='${schema!'http'}://${hostname!''}<#if port?? && port != 80>:${port!''}</#if>/im/text/${appid!''}?skill=${skill.id!''}&agent=${agent.id!''}&orgi=${orgi!''}' onclick='return openAgentChatDialog(this.href)' style='color: #777;    text-decoration: none;'> <img src='${schema!'http'}://${hostname!''}<#if port?? && port != 80>:${port!''}</#if>/images/creater.png' style='vertical-align: middle;'></img> ${agent.uname!''}</a></li>"+
								"</li>"+
								</#if>
							</#list>
							</#if>
							"</ul>"+
							</#if>
						</#if>
						</#list>
						<#elseif inviteData.onlyareaskill && inviteData.areaskilltipmsg??>
						"<div style='padding:10px 5px;text-align:center;color:#555;border-top:1px solid #dedede;'>${(inviteData.areaskilltipmsg!'')?no_esc}</div>"+
						</#if>

						"</div>"+

						"<div class='ichat-title ichat-cursor' style='color: #FFFFFF;font-weight:400;font-size: 14px;font-weight: 400;height: 28px;line-height: 28px;overflow: hidden;padding: 0 5px;background-repeat: repeat-x;text-align:right;'><#if inviteData.consult_skill_bottomtitle??>${inviteData.consult_skill_bottomtitle!''}</#if></div>"+
						"</div></div></#if>" ;

		append(document.body, '<div class="ukefu-im-point" id="ukefu-point" style="display:none;z-index:100000;font-family:14px \5FAE\8F6F\96C5\9ED1,Helvetica Neue,Helvetica,PingFang SC,Tahoma,Arial,sans-serif;position: fixed;${position};<#if inviteData?? && inviteData.skill == true && inviteData.consult_skill_fixed == false>cursor:default;<#else>cursor: pointer;</#if>">	<div class="ukefu-point-theme${inviteData.consult_vsitorbtn_model!'1'} ukefu-theme-color theme1" style="${style}${theme}" id="ukefu-point-theme"><div class="ukefu-im-point-text" id="ukefu-im-point-text" style="cursor: pointer;${text};line-height: 23px;font-size: 15px;text-align: center;margin: 0 auto;"><i style="width:24px;height:24px;display: inline-block;font: normal normal normal 14px/1 FontAwesome;font-size: inherit;text-rendering: auto;-webkit-font-smoothing: antialiased;background-image:url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAB60lEQVRIS7WV4TVsQRCEqyJABkSACBABLwJE8IjAigARIAMieCsCREAGjwja+eb03TO7O9ddrPm1Z29PV3dXdY31y8eL5I+ILUmbktYz/lXSs+2nofu9ABFBsjNJB5JWJT3MJNuR9CbpTtK5bUDnThMgIkaZnKSXtkkydyIC8BNJgI1sn88GzQFExI2kXUlHtsdDI+B7RBDPvUfbf+o7UwCZnHnv2qb9hU9EMEYKAuS4uzgByHapYqtvnkNoyRvE030Zaw0AScz7cijRZ9+Tv33b2xOAlOGjpLWvjmYWLEf1X9I2Mi4dRMRRtgVZ5WTgtaRX26cVmUj3lMtVzEPdeUTARVFfB4AsIbYG4Pe/0qbdxXERSaL7UapnKiYLIW5MTG8HGYjG6aAQlqNE+1RXVBYRUzH5H0SzF5MO2NqXJXHQ5dpAjbWKQL2jrSWo6MA2+zQlU2bOKOBi0MRaRVRq3OtcYHaT2YFDSQR8CSSTQ/itbXgpp+VFgCDbi5Z59ej+L6RKuqqTNwEqvWMbK2li97XxpSXwPhRTlPSOq7Zc99MHJxeQdt+6HcklYhdICmfonWKaZwgAh8RC8HmSXUjCjtcXtZQhAKrHGqiQUTynpTRfr1YLQwBomcq7HekdxbdG9JOF65XpMpLWOT4AygTtGd0Q7EsAAAAASUVORK5CYII=);"></i><br>${(inviteData.consult_vsitorbtn_content!'在线客服')?no_esc}</div></div><#if inviteData?? && inviteData.skill == true && inviteData.consult_skill_fixed == false>'+skillHtml+'</#if></div>');
		append(document.body, "<div class='ukefu-im-preview' id='ukefu-invite-dialog' style='z-index: 2147483648;display:none;height:177px;position: fixed;z-index:10000;<#if phone?? && phone == true>bottom:0px;width:100%;<#else>top:"+top+";width: 420px;left:"+left+";</#if>border-radius: 2px;padding: 0px;overflow: hidden;margin: 0px;${invitetheme!''}'>	<div id='ukefu-cousult-invite-dialog' class='ukefu-im-preview-bar ukefu-cousult-invite-dialog ukefu-theme-color theme1' style='padding:0px;height:100%;width:100%;background-size: cover;background:url(${schema!'http'}://${hostname!''}<#if port?? && port != 80>:${port!''}</#if>/res/image?id=${inviteData.consult_invite_bg}) no-repeat'><a href='javascript:void(0)' onclick='cskefu.refuseInvite();'><span style='float: right;width: 20px;height: 20px;color: #ffffff;font-size: 20px;' >×</span></a><div class='ukefu-consult-body' id='ukefu_consult_body' style='width:100%;height:100%;'><div class='ukefu-cousult-invite-content' id='ukefu-cousult-invite-content' style='color: #FFFFFF;width: 70%;height: 70px;line-height: 35px;font-size: 13pt;font-weight: 200;word-wrap: break-word;word-break: break-all;position: absolute;top: 30px;left: 25%;'>${inviteData.consult_invite_content!'欢迎来到本网站，请问有什么可以帮您？'}</div></div><div class='ukefu-cousult-invite-btn' style='position: absolute;bottom: 0px;right: 0px;margin: 0px 10px 10px 0px;'><button class='theme1' id='invite-btn' style='border-color:#FFFFFF !important;color:#FFFFFF;display: inline-block;height: 38px;line-height: 38px;padding: 0 18px;background-color: #009688;color: #fff;white-space: nowrap;text-align: center;font-size: 14px;margin-right:10px;border: none;border-radius: 2px;cursor: pointer;opacity: .9;filter: alpha(opacity=90);${invitetheme!''}border:1px solid #FFFFFF;' onclick='cskefu.refuseInvite();' ><#if inviteData.consult_invite_later?? && inviteData.consult_invite_later != ''>${inviteData.consult_invite_later}<#else>稍后再说</#if></button><button class='' style='display: inline-block;height: 38px;line-height: 38px;padding: 0 18px;background-color: #009688;color: #fff;white-space: nowrap;text-align: center;font-size: 14px;border: none;border-radius: 2px;cursor: pointer;opacity: .9;filter: alpha(opacity=90);background-color:#FFFFFF;color:#333333;' onclick='openInviteChatDialog();'><#if inviteData.consult_invite_accept?? && inviteData.consult_invite_accept!=''>${inviteData.consult_invite_accept}<#else>现在咨询</#if></button></div></div></div>");

		<#if inviteData?? && inviteData.skill == true && inviteData.consult_skill_fixed == false>
			document.getElementById("ukefu-im-point-text").onclick=function(){
				if(document.getElementById("ichatContent").style.display == "none"){
					document.getElementById("ichatContent").style.display = 'block';
				}else{
					document.getElementById("ichatContent").style.display = 'none';
				}
			}
		<#else>
			document.getElementById("ukefu-point").onclick=function(){
				cskefu.openChatDialog();
			}
		</#if>
		var inviteDialog =  document.getElementById('ukefu-invite-dialog');
		<#if phone?? && mobile == true>
			inviteDialog.style.bottom = 0 ;
		<#else>
			var height = document.documentElement.clientHeight ;
			var width = document.documentElement.clientWidth ;
			var top = (50 - 92*100/height)+"%";
			var left = (50 - 210*100/width)+"%" ;

			inviteDialog.style.top = top ;
			inviteDialog.style.left = left ;
		</#if>
		<#if inviteAd??>
			var inviteAdHtml =
			<#if inviteAd.adtype =="image">
			"<a href='${inviteAd.url!''}' title='${inviteAd.tiptext!''}' target='_blank'><img src='${schema!'http'}://${hostname!''}<#if port?? && port != 80>:${port!''}</#if>${inviteAd.imgurl!''}' style='max-width:420px;max-height:178px;margin:0px;vertical-align: middle;'/></a>"
			<#else>
			"<div style='padding:0px 5px 10px 5px;border-bottom:1px solid #dedede;'><a href='${inviteAd.url!''}' title='${inviteAd.tiptext!''}' target='_blank' id='point_ad_text'>${(inviteAd.content!'')?no_esc}</a></div>"
			</#if>
			var inviteBody = document.getElementById('ukefu_consult_body');
			if(inviteBody){
				inviteBody.innerHTML = inviteAdHtml ;
			}
		</#if>
		;
	},
	openChatDialog:function(){
		cskefu.openChatDialogWithURL(cskefu.chat);
	},
	openChatDialogWithURL:function(url){
		url = url + "&userid="+cskefuOnlineUserId+"&sessionid=${sessionid!''}"
		if(data){
			if(data.id){
				url = url + "&id="+data.id ;
			}
			if(data.name){
				url = url + "&name="+encodeURIComponent(data.name);
			}
			if(data.email){
				url = url + "&email="+encodeURIComponent(data.email);
			}
			if(data.phone){
				url = url + "&phone="+encodeURIComponent(data.phone);
			}
		}

		<#if inviteData?? && inviteData.skill == true && inviteData.consult_skill_fixed == true>
			url = url + "&skill=${inviteData.consult_skill_fixed_id}";
		</#if>

		url = url + "&title="+encodeURIComponent(document.title);
		<#if traceid??>
		url = url+"&traceid=${traceid}"
		</#if>
		url = url+"&url="+encodeURIComponent(location.href) ;
		try{
			window.open(url+"&t="+new Date().getTime(), "ukefu-chat", "height=600, width=850, top="+ (window.screen.availHeight - 30 - 600) /2+", left="+(window.screen.availWidth - 10 - 750)/2+",toolbar=no, menubar=no, scrollbars=no, resizable=no, directories=no,location=no, status=no");
		}catch(e){}
		return false;
	},
	display:function(){
		cskefu.writepoint();
		<#if !(inviteData.consult_vsitorbtn_display??) || (inviteData.consult_vsitorbtn_display?? && inviteData.consult_vsitorbtn_display == 0)>

			<#if inviteData?? && inviteData.consult_vsitorbtn_model != '9' && inviteData.whitelist_mode == false>
				document.getElementById("ukefu-point").style.display = "block" ;
			</#if>

			<#if inviteData.consult_invite_enable>
				<#if inviteData.consult_invite_delay?? && inviteData.consult_invite_delay == 0>
					cskefu.writeinvite() ;
				<#else>
					setTimeout(function(){
						cskefu.writeinvite() ;
					} , ${inviteData.consult_invite_delay});
				</#if>
			</#if>
		<#else>
			setTimeout(function(){
				document.getElementById("ukefu-point").style.display = "block" ;
				<#if inviteData.consult_invite_enable>
					<#if inviteData.consult_invite_delay?? && inviteData.consult_invite_delay == 0>
						cskefu.writeinvite() ;
					<#else>
						setTimeout(function(){
							cskefu.writeinvite() ;
						} , ${inviteData.consult_invite_delay});
					</#if>
				</#if>
			}, ${inviteData.consult_vsitorbtn_display});		//延时显示 访客入口
		</#if>
	},
	writeinvite:function(){
		document.getElementById('ukefu-invite-dialog').style.display = "block" ;
	},
	refuseInvite:function(){
		document.getElementById('ukefu-invite-dialog').style.display = "none" ;
		cskefu.ajax(cskefu.refuse + "&userid="+cskefuOnlineUserId+"&sessionid=${sessionid!''}&t="+new Date().getTime());
	},
	acceptInvite:function(){
		document.getElementById('ukefu-invite-dialog').style.display = "none" ;
	},
	check:function(){
		var checkTimes = 0 ;
		setInterval(function(){
			if(signal <= 0){
				checkTimes++ ;
			}else{
				checkTimes = 0 ;
			}
			if(checkTimes >= 3){	//检查当前的 Keep-Alive是否存活，检查3次都处于未存活状态，则启动
				//
				signal = 0 ;
				checkTimes = 0 ;
				cskefu.ping();
				console.log("智文客服客户端信息校对");
			}
		},5000) ;
	}

}

// 邀请聊天
function openInviteChatDialog(){
    var url = cskefu.chat + "&agent=" + cskefu.service.agentno + "&isInvite=true";
	return cskefu.openChatDialogWithURL(url);
}

// 技能组或坐席聊天
function openAgentChatDialog(url){
	return cskefu.openChatDialogWithURL(url);
}

<#if webimexist == true >
	FingerprintJS.load()
	.then(fp => fp.get())
	.then(result => {
		const { ...components } = result.components;
		const extendedComponents = {
			...components,
			<#if channelVisitorSeparate?? && channelVisitorSeparate == true>
			"cskefuAppId": { value: "${appid!''}" },
			</#if>
			"rndId": { value: (function() {
				let rnd = '';
				if(localStorage){
					rnd = localStorage.finger;
					if(!rnd){
						rnd = Math.random();
						localStorage.finger = rnd;
					}
				}
				return rnd;
			}()) },
		};

		cskefuOnlineUserId =  FingerprintJS.hashComponents(extendedComponents);
		cskefu.ajax(cskefu.in+"&userid="+cskefuOnlineUserId+"&t="+new Date().getTime() , function(data){
			if(data != "in"){
				cskefu.display();
				cskefu.ping();
				cskefu.check();
			}
		});
	});
</#if>

var onlineAddress = protocol + "://${hostname!''}<#if port?? && port != 80 && port != 443>:${port!''}</#if>";
var newscript = document.createElement('script');
newscript.setAttribute('src',onlineAddress+'/im/js/html2canvas.js');
var head = document.getElementsByTagName('head')[0];
head.appendChild(newscript);

window.addEventListener(
'message',
function(event) {
if(event.origin ===  onlineAddress && event.data === "screenshots" ){

html2canvas(document.body).then(function(canvas) {
var dataurl = canvas.toDataURL('image/png');
event.source.postMessage(dataurl, '*');
});

}
},
false
);

</#if>


