const webpack = require('webpack');
module.exports = {
  /*
  ** Headers of the page
  */
  mode: 'spa',
  head: {
    title: '家谱海,免费可视化在线数字家谱',
    meta: [
      { charset: 'utf-8' },
      { name: 'viewport', content: 'width=device-width, initial-scale=1' },
      { hid: 'Keywords', name: 'Keywords', content: '家谱海,免费可视化在线数字家谱' },
      { hid: 'Description', name: 'Description', content: '家谱世系图构建,多人协作,隐私保护,安全备份,方便导出,历史名人家谱图文知识共享,免费极简'}
    ],
    link: [
      { rel: 'icon', type: 'image/x-icon', href: '/favicon.ico' }
    ]
  },
  plugins: ['~/plugins/v-lazy-img','~/plugins/element-ui',{ src: "~/plugins/quill-editor", ssr: false },
   { src: "~/plugins/image-upload-profile", ssr: false },
   { src: "~/plugins/vue-verify", ssr: false }
  ],
  /*
  ** Customize the progress bar color
  */
  loading: { color: '#3B8070' },
  router: { mode: 'hash' },
  /*
  ** Build configuration
  */

  modules: [
    'bootstrap-vue/nuxt'
  ],
  build: {
    plugins: [
      new webpack.ProvidePlugin({
        'window.Quill': 'quill/dist/quill.js',
        'Quill': 'quill/dist/quill.js'
      })
    ],

    vendor: ['~/plugins/v-lazy-img','~/plugins/element-ui'],
    /*
    ** Run ESLint on save
    */
    extend (config, { isDev, isClient }) {
      if (isDev && isClient) {
        config.module.rules.push({
          enforce: 'pre',
          test: /\.(js|vue)$/,
          loader: 'eslint-loader',
          exclude: /(node_modules)/
        }),
        config.devtool = 'eval-source-map'
      }
    }
  }
}
