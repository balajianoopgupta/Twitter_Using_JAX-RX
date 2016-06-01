select concat(u.fname,' ',u.lname)usrname, twitterhandle 
from users u 
where not exists
(select * 
from followers 
where uname='balaji@gmail.com'
and follows=u.username
) 
and username!='balaji@gmail.com'
