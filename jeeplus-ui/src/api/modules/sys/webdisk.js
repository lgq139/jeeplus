import {post, post_upload} from "@/api/tool";

export function upload(data) {
  return post_upload('/sys/webdisk/upload', data)
}
