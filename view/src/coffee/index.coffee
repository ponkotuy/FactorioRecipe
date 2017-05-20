$(document).ready ->
  fetch('/api/items').then (res) ->
    res.json().then (json) ->
      render(json)

render = (json) ->
  new Vue
    el: '#items'
    data:
      items: groupItem(json)
    methods:
      camelCase: (str) ->
        str.charAt(0).toUpperCase() + str.substring(1)

groupItem = (items) ->
  _.groupBy items, (item) -> item.detail?.subgroup ? 'water'
