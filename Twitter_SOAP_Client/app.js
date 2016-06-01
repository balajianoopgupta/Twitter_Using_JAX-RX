var express = require('express')
    , routes = require('./routes')
    , user = require('./routes/user')
    , http = require('http')
    , home = require('./routes/functionality')
    , path = require('path')
    , ejs = require('ejs');

//URL for the sessions collections in mongoDB
//var mongoSessionConnectURL = "mongodb://localhost:27017/twitter";
var expressSession = require("express-session");
//var mongoStore = require("connect-mongo")(expressSession);
//var mongo = require("./routes/mongo");

var app = express();
app.set('port', process.env.PORT || 3000);
app.set('views', __dirname + '/views');
app.set('view engine', 'ejs');
app.use(express.favicon());
app.use(express.logger('dev'));
app.use(express.bodyParser());
app.use(express.methodOverride());
app.use(expressSession({
	secret: 'cmpe273_teststring',
	resave: false,  //don't save session if unmodified
	saveUninitialized: false,	// don't create session until something stored
	duration: 30 * 60 * 1000,    
	activeDuration: 5 * 60 * 1000,
	expires: new Date(Date.now() + 3600000),
	cookie: {maxAge: 10 * 60 * 1000},
	// store: new mongoStore({
	// 	url: mongoSessionConnectURL
	// })
}));

app.use(app.router);
app.use(express.static(path.join(__dirname, 'public')));

// development only
if ('development' === app.get('env')) 
{
  app.use(express.errorHandler());
}

//GET
//app.get('/', routes.index);
//app.get('/users', user.list);
app.get('/',function(req,res)
{
	ejs.renderFile('./views/login.ejs', function(err, result) 
	{
		if (!err) 
		{
			res.end(result);
		} 
		else 
		{
			res.end('An error Occureed while fetching values');
			console.log(err);
		}
	});
});

app.get('/signUp',home.signUp);

app.get('/homepage', function(req,res)
{
	home.fetchValues(req,res);
});

app.post('/checkLogin',home.checkLogin);

app.post('/addUser',function(req,res)
{
	console.log("Request To SignUp has been received");
	home.addUser(req,res);
});

app.post('/calculateValues',function(req,res)
{
	console.log("calulateValues->app.js");
	home.calculateValues(req,res);
});

app.post('/addTweetToDB',function(req,res)
{
	console.log("Got a call to the addTweetToDB function");
	home.addTweetToDB(req,res);
});

app.post('/renderTweets',function(req,res)
{
	home.renderTweets(req,res);
});

app.post('/renderUserTweets',function(req,res)
{
	console.log("Call to Render Only the User Tweets");
	home.renderUserTweets(req,res);	
});

app.post('/renderUserFollowing',function(req,res)
{
	console.log("Render the people the user is following");
	home.renderUserFollowing(req,res);
});

app.post('/renderUserFollowers',function(req,res)
{
	console.log("Render the people who are followers of the user");
	home.renderUserFollowers(req,res);
});

app.post('/suggestFollowers',function(req,res)
{
	home.suggestFollowers(req,res);	
});

app.post('/updateFollowers', function(req,res)
{
	home.updateFollowers(req,res);
});

app.get('/getTweetCount', function(req,res)
{
	console.log("Got a call to get the getTweetCount");
	home.getTweetCount(req,res);	
});

app.get('/userProfile', function(req,res)
{
	console.log("Call to display the userProfile page");
	home.userProfile(req,res);
});


app.post('/getUserProfile', function(req,res)
{
	console.log("Got a call to display the User Profile Info Page");
	home.getUserProfile(req,res);
});
app.post('/updateUserProfile', function(req,res)
{
	console.log("Got a call to update the User Profile Info Page");
	home.updateUserProfile(req,res);
});

app.post('/searchTweets',function(req,res)
{
	home.searchTweets(req,res);
});


app.get('/searchResults',function(req,res)
{
	console.log("In the searchResults function");
	home.searchResults(req,res);
});

app.get('/loadSearchResults',function(req,res)
{
	console.log("Loading the search results");
	home.loadSearchResults(req,res);
});

app.get('/logout',home.logout);


http.createServer(app).listen(app.get('port'), function()
{
  console.log('Express server listening on port ' + app.get('port'));
});


