<template>
  <ch-dialog
    title="表单预览"
    :visible.sync="visible"
    width="80%"
  >
    <iframe
      :src="iframeUrl"
      width="100%"
      height="500px"
      frameborder="0"
      scrolling="yes">
    </iframe>
  </ch-dialog>
</template>

<script>
import ChDialog from "@/components/ChDialog";
import config from "@/config";
export default {
  name: "ChFormPreview",
  components: {ChDialog},
  data() {
    return {
      visible: false,
      iframeUrl: ''
    }
  },

  methods: {
    showEdit(formId) {
      if (formId) {
        this.iframeUrl = config.form_preview_uri + formId
        this.visible = true
      }
    }
  }
}
</script>

<style scoped>

</style>
