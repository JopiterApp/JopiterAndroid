# Publicação (Play Store)

A publicação segue o mesmo modelo do app Petals: **GitHub Actions + git-secret + fastlane**, disparado
**manualmente** (`workflow_dispatch`) — nenhum push na `main` publica sozinho.

Pra rodar: aba **Actions** → workflow **Release** → **Run workflow**, escolhendo:

- `version_type`: `major`, `minor` ou `patch`.
- `changelog`: texto do changelog dessa versão.

O workflow [`.github/workflows/release.yaml`](.github/workflows/release.yaml) (gerado a partir de
`release.main.kts`) então:

1. Revela os segredos cifrados com **git-secret** (keystore + credenciais + service account da Play).
2. **Sobe a versão** (semver) reescrevendo `versionCode`/`versionName` em `app/build.gradle.kts`,
   escreve o changelog, faz commit, cria a tag e dá push na `main`.
3. Builda o **APK assinado** (flavor `official`) e cria um **GitHub Release** com o APK + mapping do R8.
4. Builda o **AAB assinado** e publica na **Play Store, faixa `production`**, via `fastlane`.

> ⚠️ Cada run publica em **produção**. Pra testar antes, troque a faixa para `internal` na lane do
> `fastlane/Fastfile` (lane `internal_test` já existe pronta pra isso).

## Assinatura / versão

- `versionCode = major*1_000_000 + minor*1_000 + patch` (ex.: `3.0.1` → `3000001`).

## Segredos necessários

Configure em **Settings → Secrets and variables → Actions**:

| Secret | O que é |
|---|---|
| `GPG_KEY` | Chave GPG privada (armored) capaz de descriptografar os arquivos do git-secret. |
| `RELEASE_KEY` | Chave SSH privada de um **deploy key** com acesso de escrita (para o push do bump). |

O deploy key público correspondente deve estar em **Settings → Deploy keys** com "Allow write access".

## Arquivos cifrados com git-secret

Já versionados cifrados (`*.secret`): `jopiter-key.jks`, `keystore.properties`.

**Falta adicionar** o service account da Play Store (JSON baixado do Google Cloud), uma única vez:

```bash
# com a chave GPG importada no keyring local:
cp /caminho/para/o/service-account.json fastlane/google-play.json
git secret add fastlane/google-play.json
git secret hide
git add fastlane/google-play.json.secret .gitsecret/paths/mapping.cfg
git commit -m "Add Play service account to git-secret"
```

O `keystore.properties` deve conter: `KEYSTORE_PASSWORD`, `KEYSTORE_KEY_ALIAS`, `KEYSTORE_KEY_PASSWORD`.
Os arquivos descriptografados (`jopiter-key.jks`, `keystore.properties`, `fastlane/google-play.json`)
são ignorados pelo git (só as versões `.secret` são versionadas).

## Rodar local

```bash
git secret reveal                       # descriptografa keystore + service account
./gradlew bundleOfficialRelease         # AAB assinado em app/build/outputs/bundle/officialRelease/
cd fastlane && bundle install && bundle exec fastlane playstore
```
