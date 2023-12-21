<template>
  <RouterView></RouterView>

</template>
<script setup>
import { useDark, useToggle } from '@vueuse/core';

useDark({
  selector:'html',
  arrtribute:'class',
  valueDark:'dark',
  valueLight:'light'
})
useDark({
  onChanged(dark) {useToggle(dark)}
})
</script>

<style scoped>
</style>