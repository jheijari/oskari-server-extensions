--insert to portti_bundle table
-- Add asdi lang overrides bundle to portti_bundle table
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
	'asdi-lang-overrides',
	'{}',
	'{}',
	'{
        "bundlename":"asdi-lang-overrides",
        "metadata": {
            "Import-Bundle": {
                "asdi-lang-overrides": {
                    "bundlePath":"/Oskari/packages/asdi/bundle/"
                }
            }
        }
    }'
);