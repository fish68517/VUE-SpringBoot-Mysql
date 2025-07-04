/*
 * @Author: kaix
 * @Date: 2025-07-04 12:28:53
 * @LastEditTime: 2025-07-04 12:28:56
 * @LastEditors: kaix
 * @Description: 
 */
import type { App } from 'vue';
import { createPinia } from 'pinia';

const pinia = createPinia();

export function setupStore(app: App<Element>) {
  app.use(pinia);
}

export { pinia };
