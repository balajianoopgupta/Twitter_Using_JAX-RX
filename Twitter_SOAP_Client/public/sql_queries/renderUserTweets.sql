SELECT * FROM twitter.tweets;

SELECT concat(fname,' ',lname) usrname, usr.twitterhandle,TW.TWEET 
	FROM 
	(SELECT tweet TWEET from tweets	where username='balaji@gmail.com')TW,
	users usr where username='balaji@gmail.com'
    order by TW.TWEET desc;