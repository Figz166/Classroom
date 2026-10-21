# Fig Client Codespace Handoff

Read this first when continuing in a new Codespace.

## Repository and deployment

- Repository: `Figz166/Classroom`
- Current Codespace branch: `main`. The `pages-deploy` branch also triggers the Pages workflow.
- Live site: <https://figz166.github.io/Classroom/>
- GitHub Actions deploys on pushes to `main` and `pages-deploy` using `.github/workflows/deploy-game-pages.yml`.
- The workflow builds a production standalone HTML/Wasm client. Do not copy `_site` from a local fast build into a release; the local preview is not the release artifact.
- The full source-changing Wasm link previously took about 43 minutes. CI has a cache, but it can still take a long time. Confirm the run in Actions and check the live site before claiming deployment succeeded.

## Completed visual baseline

The previous Codespace completed the title-screen visual work: six-face rotating Otherside panorama from the Modrinth project by Karbin_Drem, cyan Fig branding, a frameless left-side menu rail, and removal of the vanilla splash/fact. Attribution and CC BY-SA 4.0 details are in `THIRD_PARTY_ASSETS.md`. This visual baseline is separate from the still-in-progress modules GUI redesign. Production deployment is not verified here.

## Current status and next priority

The user tested the current local client and reports that the modules GUI and module behavior are not usable yet. Do not describe the menu/modules as finished or push another release until the issues below are addressed and retested.

- Right Shift GUI: still does not look sufficiently like Meteor. It still occupies too much screen space; the user wants a compact menu around half-screen, with small buttons/text and every label fitting inside its column. No word overflow or full-screen blur/tint.
- Module toggle feedback: clicking a module does not visibly update its state until the GUI is closed and reopened, and it may still remain stale. Make state changes refresh immediately.
- Freecam: reported not working. Verify camera movement, player isolation, and reliable enter/exit in an actual world.
- Flight: behavior reported as very bad. Do not treat it as a usable feature until tested and corrected; account for server authority.
- Block Search: does not update as new chunks load. Update/rescan when chunks arrive or the player moves, and verify in-world.
- The user said the GUI and “everything” needs work. Fix these reported issues before adding modules or claiming completion.

## Implemented prototypes (not yet accepted)

- Right-clicking a module is intended to open its settings/keybind screen. Confirm that module toggles and bindings update immediately and persist.
- `Entity ESP`: Players/Animals/Mobs/Other filters and color presets; needs in-world verification.
- `Tracers`: separate module with per-group colors and individual targets including Player, Pig, Cow, Sheep, Chicken, Zombie, Skeleton, Creeper, Enderman, and Ender Dragon. A prior build only showed tracers while moving; do not mark fixed without stationary in-world testing.
- `Chest ESP`: normal, ender, trapped chest, barrel, and shulker filters plus a color preset; needs in-world verification.
- `City ESP`: highlights discovered/loaded End City structure bounds; needs in-world verification.
- `Air Place`: on a block-item miss, sends a synthetic in-reach block-use hit through normal client prediction/server validation; needs in-world/server testing.
- `Block Search`, Waypoints, and HUD Layout are in Tools; Block Search has the reported stale-on-new-chunks bug above.
- Experimental Quick Stack, chest search, and chest rules were removed at the user's request. Do not re-add them without asking.

## Still pending

- The user requested a small top-right profile card showing the selected skin and username.
- The user requested an Alt Manager for switching saved local profiles/accounts.
- Change the title footer to say `Rewritten by Notcher2000` and move `o_xer` to the credits screen.
- AppleSkin can only be ported as native Fig code, not loaded as a Fabric/NeoForge JAR. Its source is Unlicense. Sorted is All Rights Reserved with no public source; only an independent implementation based on public behavior is allowed.
- Absolute Order's server-authoritative filters/hopper behavior cannot be enforced by a client-only Fig module on arbitrary vanilla servers/Realms.

## Validation from this work

- `./gradlew :game:compileJava --console=plain --no-daemon` passed after the current source edits.
- The local fast-analysis client was rebuilt and served from `_site` for preview. It is not a release artifact and is not a complete production/gameplay validation.
- The hosted page and Wasm returned HTTP 200. UI and ESP behavior still need browser/in-world testing; the user has not approved the current GUI as final.
- No production Pages deployment is verified until a push's Actions workflow succeeds and the live page is checked.
- Generated build outputs, `.gradle`, `node_modules`, and `_site` are local artifacts; stage only source/assets, `THIRD_PARTY_ASSETS.md`, and this handoff.

## Workflow reminders

- Preserve unrelated working-tree changes; never use destructive git cleanup/reset commands.
- The user prefers that the assistant runs commands and does not ask them to run routine commands.
- They have a limited Copilot credit budget and asked to deploy the current state before continuing larger tasks.
- Keep progress reports honest and brief. A Java compile does not verify browser gameplay, ESP rendering, Air Place, Freecam, or multiplayer inventory behavior.
