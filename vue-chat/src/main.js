// polyfill
import 'babel-polyfill';

import Vue from 'vue';
import App from './App';
import store from './store';

const axios = require('axios');
axios.defaults.baseURL = 'http://localhost:8081/'
// 全局注册，之后可在其他组件中通过 this.$axios 发送数据
Vue.prototype.$axios = axios

Vue.config.devtools = true;

new Vue({
    el: 'body',
    components: { App },
    store: store
});
