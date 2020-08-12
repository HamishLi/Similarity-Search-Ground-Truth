<template>
    <div class="HomePage">
        <!--<router-link to="/VisuallySimilarImages">Visually Similar</router-link>
        <router-view/>-->
        <div class="mb-3">
            <h1 class="text-primary">Similarity Search</h1>
        </div>

        <div class="row">
            <div class="row-cols-sm-7">
                <h3 class="text-left ml-5">Random Gallery</h3>
            </div>
            <div class="row-cols-lg-5">
                <router-link :to="{name: 'Index'}" @click.native="showRouter">
                    <span class="icon-refresh"></span>
                </router-link>
            </div>

        </div>
        <!--<div class="RandomGallery container">
            <div class="row-cols-sm-3" v-for="image in images" :key="image.id">
                <a target="_blank" v-bind:href="image.imageURL"> <img :src="image.imageURL" v-bind:alt="image.id"
                                                                      class="img-thumbnail" width="304"
                                                                      height="236"/></a>
                <router-link :to="'/VisuallySimilarImages/'+ image.id">Visual Similar
                </router-link>
            </div>
        </div>-->
        <div class="row" v-for="i in Math.ceil(images.length / 4)">
            <div class="col-sm-3" v-for="image in images.slice((i - 1) * 4, i * 4)">
                <a target="_blank" v-bind:href="image.imageURL"> <img :src="image.imageURL" v-bind:alt="image.imageId"
                                                                      class="img-responsive rounded mx-auto d-block"
                                                                      width="304"
                                                                      height="236"/></a>
                <router-link :to="'/VisuallySimilarImages/'+ image.imageId">Visual Similar
                </router-link>
            </div>
        </div>

        <div>
            <router-link :to="'/Feedback'">
                <h4 class="text-primary">Help us improve similar search performance!</h4>
            </router-link>
        </div>

        <div class="bg-secondary">
            <p class="text-light">
                These images are miniature versions of the original ones that come from the site</p>
            <a target="_blank" href="http://press.liacs.nl/mirflickr/mirdownload.html" class="text-light">
                http://press.liacs.nl/mirflickr/mirdownload.html
            </a>
            <p class="text-light">
                All rights are reserved to the author of the original image.</p>
        </div>
    </div>
</template>

<script>
    export default {
        name: "HomePage",
        data() {
            return {
                images: [],
                numberOfColumns: 4,
            }
        },
        props: {
            currentImageId: String
        },
        created: function () {
            this.getRandomImages();
        },
        methods: {
            showRouter() {
                var _this = this;
                _this.$router.go(0);
            },
            getRandomImages() {
                var _this = this;
                this.axios.get("http://localhost:8080/generateRandomImageIds")
                    .then(function (response) {
                        _this.images = response.data;
                    }).catch(function (error) {
                    console.log(error);
                })
            },
            /*getImageId(e) {
                let imageId = e.currentTarget.id;
                console.log(imageId);
                /!*let params = new URLSearchParams();
                params.append("currentImageId", this.currentImageId)*!/
                this.axios.post("/getImageId", {
                    id: imageId
                }).then(function (response) {
                    alert(response.data);
                }).catch(function (error) {
                    console.log(error);
                })
            }*/
        }
    }
</script>

<style scoped>

</style>