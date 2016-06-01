SELECT * FROM twitter.followers;

SELECT concat(fname,' ',lname) usrname, usr.twitterhandle,FB.FOLLOWEDBY 
	FROM 
    (SELECT uname FOLLOWEDBY  from followers where follows='balaji@gmail.com')FB,
    users usr where username=FB.FOLLOWEDBY;