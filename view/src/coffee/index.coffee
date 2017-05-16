$(document).ready ->
  fetch('/api/items').then (res) ->
    res.json().then (json) ->
      render(json)

render = (json) ->
  new Vue
    el: '#items'
    data:
      items: json
