
import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router);


import OrderManager from "./components/OrderManager"

import DeliveryManager from "./components/DeliveryManager"

import InventoryManager from "./components/InventoryManager"

export default new Router({
    // mode: 'history',
    base: process.env.BASE_URL,
    routes: [
            {
                path: '/orders',
                name: 'OrderManager',
                component: OrderManager
            },

            {
                path: '/deliveries',
                name: 'DeliveryManager',
                component: DeliveryManager
            },

            {
                path: '/inventories',
                name: 'InventoryManager',
                component: InventoryManager
            },



    ]
})
