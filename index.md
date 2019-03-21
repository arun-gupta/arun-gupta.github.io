# Arun Gupta's GitHub Pages

{% assign subdirs = site.static_files | map: "path" | group_by_exp: "path", "path | split: '/' | pop | join: '/'" | map: "name" | where_exp: "name", "name != ''" %}

{% for dir in subdirs %}
- [{{ dir | escape}}]({{ site.baseurl | escape}}{{ dir | escape}})
{% endfor %}
