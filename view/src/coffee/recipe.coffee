$(document).ready ->
  params = urlParameter()
  fetch("/api/recipe/#{params.id}").then (res) ->
    res.json().then (json) ->
      render(json)

render = (json) ->
  new Vue
    el:  '#recipe'
    data:
      items: json
