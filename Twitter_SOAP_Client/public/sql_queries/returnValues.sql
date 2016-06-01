SELECT * FROM twitter.users;


SELECT concat(fname,' ',lname) usrname, usr.twitterhandle, usr.phone, usr.dob,usr.location, F.FOLLOWERS, T.FOLLOWING, TW.TWEETS 
	FROM 
	(SELECT count(*) FOLLOWERS 	from followers	where follows='"+ses.user+"')F,
	(SELECT count(*) FOLLOWING 	from followers	where uname='"+ses.user+"')T,
	(SELECT count(*) TWEETS	from tweets	where username='"+ses.user+"')TW,
	users usr where username='balaji@gmail.com';