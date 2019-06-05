import { VueEditor, Quill } from "vue2-editor";
import { ImageDrop } from 'quill-image-drop-module'
import  ImageResize  from 'quill-image-resize-module';
import Vue from 'vue';
Quill.register('modules/imageDrop', ImageDrop)
Quill.register('modules/imageResize', ImageResize)
Vue.component('VueEditor', VueEditor)
