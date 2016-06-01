SELECT * FROM twitter.followers;

SELECT concat(fname,' ',lname) usrname, usr.twitterhandle,FW.FOLLOW 
	FROM 
    (SELECT follows FOLLOW  from followers where uname='balaji@gmail.com')FW,
    users usr where username=FW.FOLLOW;