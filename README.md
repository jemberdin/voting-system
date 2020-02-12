## Graduation Project

Design and implement a REST API using Hibernate/Spring/SpringMVC **without frontend**.

Build a voting system for deciding where to have lunch.

 * 2 types of users: admin and regular users
 * Admin can input a restaurant and it's lunch menu of the day (2-5 items usually, just a dish name and price)
 * Menu changes each day (admins do the updates)
 * Users can vote on which restaurant they want to have lunch at
 * Only one vote counted per user
 * If user votes again the same day:
    - If it is before 11:00 we assume that he changed his mind.
    - If it is after 11:00 then it is too late, vote can't be changed.

Each restaurant provides new menu each day.

## cURL Command Examples
### New unregistered user
Register: `curl -s -X POST -d '{"name":"testuser","email":"testuser@gmail.com","password":"password","roles":["ROLE_USER"]}' -H 'Content-Type:application/json;charset=UTF-8' http://localhost:8080/votingsystem/rest/profile/register`

### User with ADMIN role
#### User
Get all: `curl -s http://localhost:8080/votingsystem/rest/admin/users --user admin@gmail.com:admin`

Get user with id=1000: `curl -s http://localhost:8080/votingsystem/rest/admin/users/1000 --user admin@gmail.com:admin`

Create new user: `curl -s -X POST -d '{"name":"user3","email":"user3@gmail.com","password":"password","roles":["ROLE_USER"]}' -H 'Content-Type:application/json;charset=UTF-8' http://localhost:8080/votingsystem/rest/admin/users --user admin@gmail.com:admin`

Update user with id=1000: `curl -s -X PUT -d '{"name":"updatedName","email":"updatedEmail@gmail.com","password":"updatedPassword","roles":["ROLE_USER"]}' -H 'Content-Type: application/json' http://localhost:8080/votingsystem/rest/admin/users/1000 --user admin@gmail.com:admin`

Delete user with id=1000: `curl -s -X DELETE http://localhost:8080/votingsystem/rest/admin/users/1000 --user admin@gmail.com:admin`

Get by email=user1@gmail.com: `curl -s http://localhost:8080/votingsystem/rest/admin/users/by?email=user1@gmail.com --user admin@gmail.com:admin`

#### Restaurant
Create new restaurant: `curl -s -X POST -d '{"name":"New restaurant name"}' -H 'Content-Type:application/json;charset=UTF-8' http://localhost:8080/votingsystem/rest/admin/restaurants --user admin@gmail.com:admin`

Update restaurant with id=1003: `curl -s -X PUT -d '{"name":"Updated restaurant name"}' -H 'Content-Type: application/json' http://localhost:8080/votinsystem/rest/admin/restaurants/1003 --user admin@gmail.com:admin`
    
Delete restaurant with id=1003: `curl -s -X DELETE http://localhost:8080/votingsystem/rest/admin/restaurants/1003 --user admin@gmail.com:admin`


#### Menu
Get all for restaurant with id=1003: `curl -s http://localhost:8080/votingsystem/rest/admin/menus?restaurantId=1003 --user admin@gmail.com:admin`

Get menu with id=1006 for restaurant with id=1003: `curl -s http://localhost:8080/votingsystem/rest/admin/menus/1006?restaurantId=1003 --user admin@gmail.com:admin`

Create for restaurant with id=1003: `curl -s -X POST -d '{"date":"2020-02-12"}' -H 'Content-Type:application/json;charset=UTF-8' http://localhost:8080/votingsystem/rest/admin/menus?restaurantId=1003 --user admin@gmail.com:admin`

Update menu with id=1006 for restaurant with id=1003: `curl -s -X PUT -d '{"date":"2020-02-15"}' -H 'Content-Type: application/json' http://localhost:8080/votingsystem/rest/admin/menus/1006?restaurantId=1003 --user admin@gmail.com:admin`

Delete menu with id=1006 for restaurant with id=1003: `curl -s -X DELETE http://localhost:8080/votingsystem/rest/admin/menus/1006?restaurantId=1003 --user admin@gmail.com:admin`

Get by date for restaurant with id=1003: `curl -s 'http://localhost:8080/votingsystem/rest/admin/menus/by?restaurantId=1003&date=2020-02-11' --user admin@gmail.com:admin`

#### Dish
Get all for menu with id=1006: `curl -s http://localhost:8080/votingsystem/rest/admin/dishes?menuId=1006 --user admin@gmail.com:admin`

Get dish with id=1015 for menu with id=1006: `curl -s http://localhost:8080/votingsystem/rest/admin/dishes/1015?menuId=1006 --user admin@gmail.com:admin`

Create for menu with id=1006: `curl -s -X POST -d '{"name":"New dish name","price":5000}' -H 'Content-Type:application/json;charset=UTF-8' http://localhost:8080/votingsystem/rest/admin/dishes?menuId=1006 --user admin@gmail.com:admin`

Update dish with id=1015 for menu with id=1006: `curl -s -X PUT -d '{"name":"Updated dish name","price":6000}' -H 'Content-Type: application/json' http://localhost:8080/votingsystem/rest/admin/dishes/1015?menuId=1006 --user admin@gmail.com:admin`

Delete dish with id=1015 for menu with id=1006: `curl -s -X DELETE http://localhost:8080/votingsystem/rest/admin/dishes/1015?menuId=1006 --user admin@gmail.com:admin`

### User with USER role
#### User
Get profile: `curl -s http://localhost:8080/votingsystem/rest/profile  --user user1@gmail.com:password`

Update profile: `curl -s -X PUT -d '{"name":"Updated name","email":"updatedemail@gmail.com","password":"updatedPassword"}' -H 'Content-Type: application/json' http://localhost:8080/votingsystem/rest/profile --user user1@gmail.com:password`

Delete profile: `curl -s -X DELETE http://localhost:8080/votingsystem/rest/profile --user user1@gmail.com:password`

#### Restaurant
Get all: `curl -s http://localhost:8080/votingsystem/rest/restaurants --user user1@gmail.com:password`

Get restaurant with id=1004: `curl -s http://localhost:8080/votingsystem/rest/restaurants/1004 --user user1@gmail.com:password`

Get all with votes: `curl -s http://localhost:8080/votingsystem/rest/restaurants/result --user user1@gmail.com:password`

Get by name "French Restaurant": `curl -s http://localhost:8080/votingsystem/rest/restaurants/by?name=French%20Restaurant --user user1@gmail.com:password`

#### Menu
Get menus for voting: `curl -s http://localhost:8080/votingsystem/rest/menus --user user1@gmail.com:password`

#### Vote
Get all: `curl -s http://localhost:8080/votingsystem/rest/votes --user user1@gmail.com:password`

Get vote with id=1043: `curl -s http://localhost:8080/votingsystem/rest/votes/1043  --user user1@gmail.com:password` 

Create vote for restaurant with id=1003: `curl -s -X POST -H 'Content-Type:application/json;charset=UTF-8' http://localhost:8080/votingsystem/rest/votes?restaurantId=1003 --user user1@gmail.com:password`

Update vote with id=1043 for restaurant with id=1004: `curl -s -X PUT -H 'Content-Type: application/json' http://localhost:8080/votingsystem/rest/votes/1043?restaurantId=1004 --user user1@gmail.com:password`