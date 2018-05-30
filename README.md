This project serves to:

1. Provide CRUD API for MT configurations (data that is specific to how Company connects to MT providers)
2. Provide CRUD API for MT languages (pairs of source and target languages that Company wants to use MT on)

The current endpoints and methods are as follows:

```
/config/{id} [GET, DELETE]
/config [PUT, POST]
/configs [GET]
/lang/{id} [GET, DELETE]
/lang [PUT, POST]
/langs [GET]
/pairs [GET]
/synchronized-configs [GET]
/kantan-engines [GET]
```

A sample MT configuration response:

```
{
	"Id":2,
	"Target":"en;fr;it;de;es;nl;pl;*;",
	"Variant":"Boulder Account",
	"Source":"en;fr;it;de;es;nl;pl;*;",
	"Description":"Generic Microsoft NMT",
	"Params":"{\"azureKey\":\"uht3b4eXP\"}",
	"Site":"boulder",
	"ConnectorId":"microsoft",
	"Sequence":10,
	"ClientId":0
}
```

A sample MT language response:

```
{
	"Id":2,
	"Target":"it",
	"Source":"en",
	"Pair":"en:it"
}
```

A sample Kantan Engine response:

```
{
	"ter": "0.27411",
	"processedwc": "280140",
	"src": "en-us",
	"trg": "pt-br",
	"wc": "29502584",
	"trgname": "Portuguese (Brazil)",
	"uniquewc": "394341",
	"library": "",
	"srcname": "English (USA)",
	"name": "Portuguese_BR_JD",
	"alias": "KantanTranslate",
	"fmeasure": "0.794646",
	"bluescore": "59.24"
}
```
