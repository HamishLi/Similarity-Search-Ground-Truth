import Vue from "vue";
import VueRouter from "vue-router";
import NotFoundComponent from "../components/NotFoundComponent";


Vue.use(VueRouter);

const routes = [
    {
        path: '*',
        component: NotFoundComponent
    },
    {
        path: "/",
        redirect: "/HomePage"
    },
    {
        path: "/HomePage",
        component: () => import('../views/HomePage.vue'),
        meta: {title: 'Home Page'}
    },
    {
        path: '/VisuallySimilarImages/:imageId?',
        component: () => import(/* webpackChunkName: "dashboard" */ '../views/VisuallySimilarImages.vue'),
        meta: {title: 'Visually Similar'}
    },
    {
        path: '/Feedback',
        component: () => import(/* webpackChunkName: "dashboard" */ '../views/Feedback.vue'),
        meta: {title: 'Feedback'}
    }
]

const router = new VueRouter({
    mode: "history",
    base: process.env.BASE_URL,
    routes
});

export default router;
