<template>
  <ch-dialog
    title="通告预览"
    :visible.sync="visible"
    width="70%"
  >
    <div v-html="content"></div>
  </ch-dialog>
</template>

<script>
import ChDialog from "@/components/ChDialog";
export default {
  name: "NoticePreview",
  components: {ChDialog},
  data() {
    return {
      visible: false,
      content: ''
    }
  },
  methods: {
    showEdit(row) {
      if (row) {
        this.content = row.content
        this.visible = true
      }
    }
  }
}
</script>

<style scoped>

</style>
