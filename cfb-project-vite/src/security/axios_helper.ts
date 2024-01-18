import axios, { Method } from "axios";

axios.defaults.baseURL = 'http://localhost:8080'
axios.defaults.headers.post["Content-Type"] = 'application/json'

export const request = (method: Method, url: string, data: any) => {
    return axios({
        method: method,
        url: url,
        data: data
    })
}