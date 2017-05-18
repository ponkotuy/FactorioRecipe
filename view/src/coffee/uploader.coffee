$(document).ready ->
  fetch('/api/recipes/versions').then (res) ->
    res.json().then (json) ->
      render(json)

render = (json) ->
  new Vue
    el: '#versions'
    data:
      versions: json
      password: ''
    methods:
      deleteRecipe: (version) ->
        fetch("/api/recipes/#{version}", {method: 'delete'}).then (res) ->
          location.reload(false)
