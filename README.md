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
load the sample data:

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
