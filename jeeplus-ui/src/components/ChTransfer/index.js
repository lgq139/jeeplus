import Transfer from './main';

/* istanbul ignore next */
Transfer.install = function(Vue) {
Vue.component(Transfer.name, Transfer);
};

export default Transfer;
