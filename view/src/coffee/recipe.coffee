$(document).ready ->
  params = urlParameter()
  fetch("/api/recipe/#{params.id}").then (res) ->
    res.json().then (json) ->
      render(params.id, json)
  fetch("/api/item/#{params.id}").then (res) ->
    res.json().then (json) ->
      renderItem(json)

render = (itemId, json) ->
  new Vue
    el:  '#recipe'
    data:
      items: json
      itemId: itemId

renderItem = (json) ->
  new Vue
    el: '#item'
    data:
      item: json
