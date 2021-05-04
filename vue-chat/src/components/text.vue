<script>
import { actions } from '../store';

export default {
    vuex: {
        actions: actions
    },
    data () {
        return {
            content: {
              body: '',
              self: true
            },
            answer: {
              body: '',
              self: false
            }
        };
    },
    methods: {
        onKeyup () {
            if (this.content.body.length) {
              this.content.self = true
              this.sendMessage(this.content);
              let ques = this.content.body
              this.content.body = ''
              this.$axios.post('/question', {
                question: ques
              }).then(resp => {
                if (resp.status === 200) {
                  this.answer.body = resp.data
                  this.sendMessage(this.answer)
                } else {
                  console.log("fail")
                }
              })
            }
        }
    }
};
</script>

<template>
<div class="text">
    <textarea placeholder="按 Enter 发送" v-model="content.body" v-on:keyup.enter="onKeyup"></textarea>
</div>
</template>

<style lang="less" scoped>
.text {
    height: 160px;
    border-top: solid 1px #ddd;

    textarea {
        padding: 10px;
        height: 100%;
        width: 100%;
        border: none;
        outline: none;
        font-family: "Micrsofot Yahei";
        resize: none;
    }
}
</style>
