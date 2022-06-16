import Axios from "axios";
import config from "@/config";
//
function getAxiosInstance() {
  return Axios.create({
    baseURL: config.window_uri
  })
}

// 获取热点场景式项目申报统计情况
export function getProjectCountByCatalog() {
  return getAxiosInstance().get('/api/service/count/projectCountByCatalog' +
    '?client_id=F0wptALZff&time=20220521113245&sign=b4c2510469b7ad0036ec35c85f956e59')
}


