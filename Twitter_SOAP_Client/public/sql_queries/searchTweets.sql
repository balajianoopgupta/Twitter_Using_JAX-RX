SELECT * FROM twitter.tweets;

SELECT concat(fname,' ',lname) usrname, usr.twitterhandle, t.tweet
from tweets t, users usr
where t.username=usr.username and t.tweet like '%just%';