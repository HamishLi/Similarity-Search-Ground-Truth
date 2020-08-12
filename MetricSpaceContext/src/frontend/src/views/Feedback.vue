<template>
    <template>
        <div class="Feedback">
            <h1 class="text-primary">Give Us Some Feedback in This Page</h1>
            <div class="row mt-2">
                <div class="col-sm-8 bg-light">
                    <h4>Which set of images would you think is more similar to the original one?</h4>
                </div>
                <div class="col-sm-4 bg-light">
                    <a target="_blank"
                       v-bind:href="imageURL"> <img :src="imageURL" class="img-responsive rounded mx-auto d-block"
                                                    width="152"
                                                    height="118"/></a>
                </div>
            </div>

            <div class="row mt-3 result_1.0">
                <div class="radio-button col-sm-3 flex-column justify-content-center text-center text-success">
                    <input type="radio" id="RadioButton1" value="pow1.0" v-model="radioButton"> Choose
                </div>
                <div class="result_1.0" v-for="i in Math.ceil(images10.length / 3)">
                    <div class="col-sm-3 mt-1" v-for="image in images10.slice((i - 1) * 3, i * 3)">
                        <a target="_blank" v-bind:href="image.imageURL"> <img :src="image.imageURL"
                                                                              v-bind:alt="image.imageId"
                                                                              class="img-responsive rounded mx-auto d-block"
                                                                              width="203"
                                                                              height="158"/></a>
                    </div>
                </div>
            </div>

            <div class="row mt-3 result_3.0">
                <div class="radio-button col-sm-3 flex-column justify-content-center text-center text-success">
                    <input type="radio" id="RadioButton2" value="pow3.0" v-model="radioButton"> Choose
                </div>
                <div class="result_3.0" v-for="i in Math.ceil(images30.length / 3)">
                    <div class="col-sm-3 mt-1" v-for="image in images30.slice((i - 1) * 3, i * 3)">
                        <a target="_blank" v-bind:href="image.imageURL"> <img :src="image.imageURL"
                                                                              v-bind:alt="image.imageId"
                                                                              class="img-responsive rounded mx-auto d-block"
                                                                              width="203"
                                                                              height="158"/></a>
                    </div>
                </div>
            </div>

            <div class="row mt-3 result_3.5">
                <div class="radio-button col-sm-3 text-center text-success">
                    <input type="radio" id="RadioButton3" value="pow3.5" v-model="radioButton"> Choose
                </div>
                <div class="result_3.5" v-for="i in Math.ceil(images35.length / 3)">
                    <div class="col-sm-3 mt-1" v-for="image in images35.slice((i - 1) * 3, i * 3)">
                        <a target="_blank" v-bind:href="image.imageURL"> <img :src="image.imageURL"
                                                                              v-bind:alt="image.imageId"
                                                                              class="img-responsive rounded mx-auto d-block"
                                                                              width="203"
                                                                              height="158"/></a>
                    </div>
                </div>
            </div>

            <div class="pull-right mt-3">
                <button class="btn btn-primary" @click="postFeedback">Submit</button>
            </div>

            <div class="pull-right mt-3">
                <router-link :to="'/HomePage'">
                    <h4 class="text-primary">Back to Home Page</h4>
                </router-link>
            </div>

            <div class="bg-secondary mt-3">
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
</template>

<script>
    export default {
        name: "Feedback",
        data() {
            return {
                images10: [],
                images30: [],
                images35: [],
                imageId: '',
                imageURL: '',
                radioButton: 'notChosen'
            }
        },
        created: function () {
            this.getRandomImage();
        },
        methods: {
            getRandomImage() {
                var _this = this;
                this.axios.get("http://localhost:8080/generateRandomFeedbackId")
                    .then(function (response) {
                        _this.imageId = response.data;
                        _this.imageURL += "https://similarity.cs.st-andrews.ac.uk/mirflkr/images/";
                        _this.imageURL += Math.floor(_this.imageId / 10000);
                        _this.imageURL += "/";
                        _this.imageURL += _this.imageId;
                        _this.imageURL += ".jpg";
                        _this.getPoweredGroundTruth();
                    }).catch(function (error) {
                    console.log(error);
                })
            },
            getPoweredGroundTruth() {
                var _this = this;
                var url = "http://localhost:8080/getPoweredImages/1.0/";
                url += _this.imageId;
                this.axios.get(url)
                    .then(function (response) {
                        _this.images10 = response.data;
                    }).catch(function (error) {
                    console.log(error);
                });

                url = "http://localhost:8080/getPoweredImages/3.0/";
                url += _this.imageId;
                this.axios.get(url)
                    .then(function (response) {
                        _this.images30 = response.data;
                    }).catch(function (error) {
                    console.log(error);
                });

                url = "http://localhost:8080/getPoweredImages/3.5/";
                url += _this.imageId;
                this.axios.get(url)
                    .then(function (response) {
                        _this.images35 = response.data;
                    }).catch(function (error) {
                    console.log(error);
                })
            },
            postFeedback() {
                var _this = this;
                console.log(_this.radioButton);
                /*let params = new URLSearchParams();
                params.append("currentImageId", this.currentImageId)*/
                var params = new URLSearchParams()
                params.append('imageId', _this.imageId)
                params.append('result', _this.radioButton)
                this.axios.post("/postFeedback", params).then(function (response) {
                    console.log(response.data);
                    if (response.data === "error") {
                        alert(response.data + "! please select a set before submit.");
                    } else {
                        _this.$router.go(0);
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            }
            /*postFeedback() {
                var _this = this;
                let url = 'http://localhost:8080/postFeedback/';
                url += _this.imageId;
                url += '/';
                url += _this.radioButton;
                console.log(url);
                axios.get(url).then(function (resp) {
                    console.log(resp.data)
                })
            }*/
        }
    }
</script>

<style scoped>
    .col-center-block {
        position: absolute;
        top: 50%;
        -webkit-transform: translateY(-50%);
        -moz-transform: translateY(-50%);
        -ms-transform: translateY(-50%);
        -o-transform: translateY(-50%);
        transform: translateY(-50%);
    }

    .row-margin-top {
        margin-top: 20px;
    }
</style>