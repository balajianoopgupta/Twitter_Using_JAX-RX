INSERT INTO `twitter`.`followers` (`uname`, `follows`)
SELECT 'balaji@gmail.com', username 
FROM users u where twitterhandle='sudhi';