--insert to portti_bundle table
-- Add asdi login bundle to portti_bundle table
INSERT
INTO portti_bundle
(
	name,
	config,
	state,
	startup
)
VALUES
(
	'asdi-login',
	'{}',
	'{}',
	'{
            "instanceProps":{},
            "title":"ASDI login",
            "bundleinstancename":"asdi-login",
            "fi":"asdi-login",
            "sv":"asdi-login",
            "en":"asdi-login",
            "bundlename":"asdi-login",
            "metadata": {
                "Import-Bundle": {
                    "asdi-login": {
                        "bundlePath":"/Oskari/packages/asdi/bundle/"
                    }
                },
                "Require-Bundle-Instance": []
            }
    }'
);