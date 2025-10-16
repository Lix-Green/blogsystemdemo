import request from './index';

export const getMessages = () => request({url: '/messages', method: 'GET'});
export const addMessage = (data) => request({url: '/messages', method: 'POST', data});
