<template>
    <div class="VisuallySimilarImages">
        <h1>View visually Similar Images in This Page</h1>
        <div class="row mt-3" v-for="i in Math.ceil(images.length / 4)">
            <div class="col-sm-3" v-for="image in images.slice((i - 1) * 4, i * 4)">
                <p class="mb-0">{{image.distance}}</p>
                <a target="_blank" v-bind:href="image.imageURL"> <img :src="image.imageURL" v-bind:alt="image.imageId"
                                                                      class="img-responsive rounded mx-auto d-block"
                                                                      width="304"
                                                                      height="236"/></a>
                <router-link :to="'/VisuallySimilarImages/'+ image.imageId">Visual Similar
                </router-link>
            </div>
        </div>


        <div class="row">
            <div class="col-sm-6">
                <router-link :to="'/Feedback'">
                    <h4 class="text-primary">Help us improve similar search performance!</h4>
                </router-link>
            </div>
            <div class="col-sm-6">
                <router-link :to="'/HomePage'">
                    <h4 class="text-primary">Back to Home Page</h4>
                </router-link>
            </div>
        </div>
    </div>
</template>

<script>
    export default {
        name: "VisuallySimilarImages",
        data() {
            return {
                images: [],
            }
        },
        created: function () {
            this.getGroundTruth();
        },
        methods: {
            getGroundTruth() {
                var _this = this;
                var id = this.$route.params.imageId;
                var url = "http://localhost:8080/getImages/";
                url += id;
                this.axios.get(url)
                    .then(function (response) {
                        _this.images = response.data;
                    }).catch(function (error) {
                    console.log(error);
                })
            },
        }
    }
</script>

<style scoped>

</style>