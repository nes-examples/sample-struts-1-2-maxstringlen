# Struts 1.2 `maxStringLen` example

[HeroDevs NES for Apache Struts 1.2](https://docs.herodevs.com/apache-struts) limits each text field in a multipart form to `maxStringLen` bytes, 4K by default. The limit is the fix for [CVE-2023-34396](https://www.herodevs.com/vulnerability-directory/cve-2023-34396). When a field goes over it, Struts drops that field and then doesn't populate the ActionForm at all. Every field and attachment arrives empty, and the user isn't told why.

This app shows that behavior next to the recommended handling: raise the limit to fit your real data, and catch the over-limit case so the user gets a message instead of an empty save. The same app runs four times, with and without Tiles.

## The four copies

|                   | Before: Struts defaults (4K)                                     | After: recommended (32K)                                       |
|-------------------|------------------------------------------------------------------|----------------------------------------------------------------|
| **With Tiles**    | [`/tiles/before/`](http://localhost:8080/tiles/before/)          | [`/tiles/after/`](http://localhost:8080/tiles/after/)          |
| **Without Tiles** | [`/plain/before/`](http://localhost:8080/plain/before/)          | [`/plain/after/`](http://localhost:8080/plain/after/)          |

All four use the Validator plug-in, with the title as a required field. A bar at the top of each page switches between them.

Each page header reads the Tiles plug-in, request processor and `maxStringLen` from the running Struts configuration, and names the Struts and Commons FileUpload jars in the WAR. On `/tiles/before/` the processor shows as `TilesRequestProcessor` even though `struts-config.xml` names `RequestProcessor`, because the Tiles plug-in swaps it in.

## Requirements

- JDK 8
- Docker with `docker compose`
- An NES access token. See [Access Tokens](https://docs.herodevs.com/additional-information/access-tokens).

## Run it

```bash
NES_TOKEN=<your NES access token> ./mvnw -s settings.xml clean package
docker compose up --build -d
```

Open <http://localhost:8080/>, which starts on `/tiles/before/`. Stop it with `docker compose down`. To use another port, run `PORT=9090 docker compose up --build -d`.

Maven downloads `struts:struts:1.2.9-struts-1.2.10` from the NES registry. `settings.xml` reads the token from `NES_TOKEN`, so it never has to be written to a file. If you already have the registry in `~/.m2/settings.xml`, drop the `-s settings.xml`.

## Things to try

The form has buttons that fill the description with 5,000 or 40,000 characters, or with 1,400 or 12,000 Chinese characters. A counter under it shows the size in bytes. There are two save buttons, one for an action mapping with `validate="true"` and one for `validate="false"`.

On a **before** copy:

1. Fill 5,000 characters and save with `validate="true"`. The page says "Title is required." even though the title was sent, because Struts never copied it into the form.
2. Do the same with `validate="false"`. The save runs, and the title, description and attachment are all null.
3. Fill 1,400 Chinese characters. That's fewer than 4,096 characters, but it's 4,200 bytes in UTF-8, so it's over the limit too.

On an **after** copy:

1. Fill 5,000 characters. It saves, because the limit is now 32K.
2. Fill 40,000 characters and save with either button. You come back to the form with a "too long" message, and the save doesn't run.

## The fix

The after copies change one line in `struts-config.xml`:

```xml
<!-- with Tiles -->
<controller processorClass="example.TilesLimitAwareRequestProcessor" maxStringLen="32K"/>

<!-- without Tiles -->
<controller processorClass="example.LimitAwareRequestProcessor" maxStringLen="32K"/>
```

**`maxStringLen`.** Set it to the largest text value the application accepts. There's no setting for unlimited, because each text field is held in memory as a string, and that's what the CVE is about. The limit counts bytes, not characters: English text takes one byte per character, but Chinese takes three, so 32K holds about 32,000 English characters or about 10,000 Chinese ones. The limit applies to each text field separately, not to the whole form.

**The request processor.** [`LimitAwareRequestProcessor`](src/main/java/example/LimitAwareRequestProcessor.java) overrides `processValidate()`. When Struts has set the `MaxStringLengthExceeded` request attribute, it rolls back the upload and adds the `error.text.tooLong` message. Then it forwards to a `textTooLong` forward if the action defines one, and to the action's `input` page otherwise. Because it runs before validation and before the action, it covers `validate="false"` mappings too.

**With Tiles, extend `TilesRequestProcessor`.** [`TilesLimitAwareRequestProcessor`](src/main/java/example/TilesLimitAwareRequestProcessor.java) has the same body. The Tiles plug-in refuses any other `RequestProcessor` subclass, and Struts doesn't start:

```text
SEVERE ... TilesPlugin : Specified RequestProcessor not compatible with TilesRequestProcessor
```

**Per form instead.** If you'd rather not change the processor, [`CheckedEditForm`](src/main/java/example/CheckedEditForm.java) does the same check in the form's `validate()` method. That only runs on mappings with `validate="true"`.

**The form comes back empty.** Struts never populated it, so the input page can't show what the user typed. A limit in the browser stops most oversized submissions before they're sent, as the byte counter here does. A `maxlength` attribute on a `textarea` counts characters, not bytes, so for non-Latin text keep it to about a third of `maxStringLen`.

## Scripted requests

`/scripted` is the same app with Struts' defaults, no Tiles and plain-text pages, for `curl`:

```bash
printf '%*s' 5000 '' | tr ' ' x > long.txt
curl -F method=save -F title=T -F "description=<long.txt" http://localhost:8080/scripted/saveUnvalidated.do
```

| Mapping            | Form                               | Result with a 5,000-byte description          |
|--------------------|------------------------------------|-----------------------------------------------|
| `saveValidated`    | `EditForm`, `validate="true"`      | "Title is required."                          |
| `saveUnvalidated`  | `EditForm`, `validate="false"`     | Saved, with every field null                  |
| `saveChecked`      | `CheckedEditForm`, `validate="true"` | The "too long" message                      |

Struts logs each over-limit field. `docker compose logs` shows a line like this:

```text
WARNING [...] org.apache.struts.upload.CommonsMultipartRequestHandler.addTextParameter Max-String-Length: FieldName: description, FileName: null, MaxSize: 4096, CurrentSize: 5000
```

## More information

- [NES for Apache Struts 1.x File Upload](https://docs.herodevs.com/apache-struts/additional-info/file-upload): every upload limit, its default, and what happens when a request exceeds it.

## License

The application code is licensed under the [Apache License 2.0](LICENSE).
