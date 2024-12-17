
str=(-H "content-type: application/json" -H "x-meow: this is a really absolutely secure token that will prevent all spam in user creation" -X POST localhost:8080/api/db/)

echo "deleting"
curl "${str[@]}delete_user" --data '"hewwow"' 
echo
curl "${str[@]}delete_user" --data '"hewwow2"' 
echo
curl "${str[@]}delete_user" --data '"hewwow3"' 
echo

echo
echo "creating"
curl "${str[@]}create_user" -X POST --data '{"id": "hewwow", "name": "meow"}' 
echo
curl "${str[@]}create_user" -X POST --data '{"id": "hewwow2", "name": "nyaa"}'
echo
curl "${str[@]}create_user" --data '{"id": "hewwow3", "name": "lfkjfkljas"}'
echo

echo
echo "get"
curl "${str[@]}get_user" --data '"hewwow"'
echo

echo
echo "update"
curl "${str[@]}update_user" --data '{"id": "hewwow", "name": "meow updated"}'
echo

echo
echo "get"
curl "${str[@]}get_user" --data '"hewwow"'
echo

echo
echo "creating walk"
w1=$(curl --silent "${str[@]}create_walk" --data '{"user_id": "hewwow", "walk": {"start": 0, "end": 7, "name": "howard", "rating": 0.9}, "conditions": []}')
echo $w1
w2=$(curl --silent "${str[@]}create_walk" --data '{"user_id": "hewwow", "walk": {"start": 10, "end": 15, "name": "asdfjkghaow8ry", "rating": 0.9}, "conditions": []}')
echo $w2
echo

echo
echo "get walks"
curl "${str[@]}list_walks" --data '{"user_id": "hewwow", "start": 0, "end": 1}'
echo


echo
echo "list walk info"
curl "${str[@]}list_walk_info" --data "{\"user_id\": \"hewwow\", \"walk_id\": $w1}"
echo
curl "${str[@]}list_walk_info" --data "{\"user_id\": \"hewwow\", \"walk_id\": $w2}"
echo


echo
echo "updating walk"
curl "${str[@]}update_walk" --data "{\"user_id\": \"hewwow\", \"walk_id\": $w1, \"rating\": 22222}"
echo

echo
echo "get walks"
curl "${str[@]}list_walks" --data '{"user_id": "hewwow", "start": 0, "end": 1}'
echo


echo
echo "delete walk"
curl "${str[@]}delete_walk" --data "{\"user_id\": \"hewwow\", \"walk_id\": $w1}"
echo

echo
echo "get walks"
curl "${str[@]}list_walks" --data '{"user_id": "hewwow", "start": 0, "end": 500}'
echo