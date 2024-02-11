# Google Form Submitter

Plugin to submit monster drops to a Google Form, specified by the settings.

Intended to be used by Oblivion, but will also work for other purposes.

## Settings

`Whitelisted RSN`: The RSN of the account to submit drops from.

`Google Form ID`: 40 alphanumeric characters in the URL.

`IBB Api Key`: API Key for IBB. Can be generated at [api.imgbb.com](https://api.imgbb.com).

`Misc Key/Value Pairs`: Miscellaneous key/value pairs for the Google Form. Will just get appended to the submission URL.

`NPC Name Entry Key`: Entry key for the NPC field in the Google Form.

`Item Name Entry Key`: Entry key for the item field in the Google Form.

`Image Entry Key`: Entry key for the screenshot URL field in the Google Form.

`Solo Chambers Entry Key`: Entry key for solo chambers checkbox. Used by Oblivion.

`Allow Seasonal/Beta Worlds`: Allow drops on seasonal/beta/tournament/fresh start worlds.

`Drop Mapping URL`: URL to retrieve the drop mapping document from. Leave blank if it is not being used.

`Drop Mapping`: Maps the in-game NPC name and item name, to the submission NPC name and item name.

        NPC Name,Item ID,Submission NPC Name,Submission Item Name

Example drop mapping: [https://github.com/umer-rs/google-form-submitter/blob/oblv-mapping/drop-mapping.txt](https://github.com/umer-rs/google-form-submitter/blob/oblv-mapping/drop-mapping.txt)
