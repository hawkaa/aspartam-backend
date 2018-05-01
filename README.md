# Aspartam Backend

![Build status](https://circleci.com/gh/hawkaa/aspartam-backend.svg?style=shield&circle-token=:circle-token)

#### Running
Aspartam Backend requires DynamoDB to run. The development configuration is set
up to use a local instance provided by LocalStack. To install it, start it, and
load it with test data, run:
```
./bin/start-dynamo.sh
```

Start the server:

```
./gradlew start
```

API is now available at `http://localhost:9000/`. Before you start, you need to
provision the table and load the sample data:

```
curl http://localhost:9000/api/reset
```

#### API Documentation

*  `GET /api/polygons` returns all available polygons formatted as a GeoJSON
`FeatureCollection`.
* `POST /api/polygons` accepts a GeoJSN `FeatureCollection`.
* `GET /api/reset` resets the polygon data.


#### Testing
Unit tests:
```
./gradlew test
```

Integration tests: Doesn't exsist, unfortunately.

## Future work
This API has some sort of weird mismatch between the underlying data structure.
It accepts and returns full FeatureCollection GeoJSON items, but store them as
separate features. It could be that the API itself should have polygon-level
granularity, and return a list of the features in the database.

The features are also stored as strings, even though they are JSON objects.
Seems okay for now, but in the future, we might want to leverage DynamoDB and
define mulitple fields for the feature data.

The API is far from production ready. It assumes a local DynamoDB and is not
flexible in terms of configuring AWS credentials. DynamoDB table should have
been provisioned via cloudformation, and not via the reset endpoint.

Last, but perhaps more importantly, it lacks integration tests that test the API
from HTTP all the way down to DynamoDB and back again.
