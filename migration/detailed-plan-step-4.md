# Step 4 — Copy and Inline `sjukfall-engine`

## Problem
The `sjukfall-engine` infra library is the core sick-leave calculation engine used across 4 rehabstod
modules (common, web, it-integration, sparrtjanst-integration). It must be copied into the local
project so the infra dependency can eventually be removed (Step 11). The infra JAR stays on the
classpath for now — this step is purely additive.

## Approach
Copy all 22 source files from `infra/sjukfall/engine/src/main/java` into a new local package
`se.inera.intyg.rehabstod.sjukfall` in the **common** module (since it's used by common,
it-integration, sparrtjanst-integration, and web — all of which already depend on common).
Then update imports in ~35 consuming files and update `SjukfallConfig` to stop component-scanning
the infra package.

## Key Risk: Name Clashes
The infra sjukfall-engine defines classes with names that already exist in rehabstod:
- **`DiagnosKapitel`** (infra) vs `se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKapitel` (web)
- **`Patient`** (infra) vs `se.inera.intyg.rehabstod.web.model.Patient` (web)
- **`Patient`** (infra) vs `se.inera.intyg.rehabstod.common.logmessages.Patient` (common, from Step 2)

These classes are in different packages so there's no compile conflict, but some files may already
import both (e.g. `import ...sjukfall.dto.Patient` alongside `import ...web.model.Patient`).
The fix is simply to use fully-qualified names where both are needed, or ensure the correct import
is used. This needs careful attention per-file during import replacement.

## Files to Copy (22 source files)

### DTOs (`se.inera.intyg.rehabstod.sjukfall.dto` — 16 files)
| Infra class | Notes |
|---|---|
| `IntygData` | Core DTO, parent of SjukfallIntyg |
| `IntygParametrar` | Request parameters |
| `SjukfallEnhet` | Output DTO for unit-level sick leave |
| `SjukfallPatient` | Output DTO for patient-level sick leave |
| `SjukfallIntyg` | Internal, extends IntygData — references engine + util classes |
| `DiagnosKod` | Uses `commons-lang3` StringUtils |
| `DiagnosKapitel` | Range-based diagnosis grouping |
| `DiagnosKategori` | Single diagnosis category |
| `Formaga` | Work capacity period |
| `Lakare` | Doctor info |
| `Patient` | Patient info (distinct from web.model.Patient and logmessages.Patient) |
| `Vardgivare` | Care provider |
| `Vardenhet` | Care unit |
| `RekoStatusDTO` | Uses Lombok @Data |
| `RekoStatusTypeDTO` | Uses Lombok @Data |
| `OccupationTypeDTO` | Uses Lombok @Data |

### Engine classes (`se.inera.intyg.rehabstod.sjukfall.engine` — 6 files)
| Infra class | Notes |
|---|---|
| `SjukfallIntygEnhetCreator` | Creates enhet-level certificate maps |
| `SjukfallIntygEnhetResolver` | Resolves enhet-level sjukfall |
| `SjukfallIntygPatientCreator` | Creates patient-level certificate maps |
| `SjukfallIntygPatientResolver` | Resolves patient-level sjukfall |
| `SjukfallLangdCalculator` | Calculates sick-leave duration with interval merging |
| `LocalDateInterval` | Helper for date-range arithmetic |

### Service classes (`se.inera.intyg.rehabstod.sjukfall.services` — 3 files)
| Infra class | Notes |
|---|---|
| `SjukfallEngineService` | Interface |
| `SjukfallEngineServiceImpl` | `@Service("sjukfallEngineService")` — already has Spring annotation |
| `SjukfallEngineServiceException` | RuntimeException |

### Utility classes (`se.inera.intyg.rehabstod.sjukfall.util` — 2 files)
| Infra class | Notes |
|---|---|
| `Mapper` | Abstract base with Map.Entry helpers |
| `SysselsattningMapper` | Maps occupation codes to Swedish names |

**Total: 27 source files** (16 DTOs + 6 engine + 3 services + 2 utils)

## Build Changes

### `common/build.gradle`
- Add `implementation "org.apache.commons:commons-lang3"` (needed by `DiagnosKod`)
- The `sjukfall-engine` dependency stays for now (removed in Step 11)

### `web/src/main/java/.../config/SjukfallConfig.java`
- Remove `@ComponentScan("se.inera.intyg.infra.sjukfall.services")`
- The local `SjukfallEngineServiceImpl` already has `@Service` so Spring will pick it up
  from the common module via existing component scanning

## Import Updates (~35 files)

### Replacement pattern
`se.inera.intyg.infra.sjukfall.dto.X`       → `se.inera.intyg.rehabstod.sjukfall.dto.X`
`se.inera.intyg.infra.sjukfall.services.X`   → `se.inera.intyg.rehabstod.sjukfall.services.X`
`se.inera.intyg.infra.sjukfall.engine.X`     → `se.inera.intyg.rehabstod.sjukfall.engine.X`
`se.inera.intyg.infra.sjukfall.util.X`       → `se.inera.intyg.rehabstod.sjukfall.util.X`

### Files by module

**common** (1 file):
- `IntygAccessControlMetaData.java` — imports `IntygData`

**it-integration** (4 main files):
- `PopulateFiltersResponseDTO.java` — imports DiagnosKapitel, Lakare, OccupationTypeDTO, RekoStatusTypeDTO
- `RekoStatusDTO.java` — imports RekoStatusTypeDTO
- `SickLeavesRequestDTO.java` — imports DiagnosKapitel
- `SickLeavesResponseDTO.java` — imports SjukfallEnhet

**sparrtjanst-integration** (4 main + 2 test files):
- `SparrtjanstClientService.java` — imports IntygData
- `SparrtjanstClientServiceImpl.java` — imports IntygData
- `SparrtjanstIntegrationService.java` — imports IntygData
- `SparrtjanstIntegrationServiceImpl.java` — imports IntygData
- `SparrtjanstClientServiceImplTest.java` — imports IntygData
- `SparrtjanstIntegrationServiceImplTest.java` — imports IntygData

**web** (11 main + 9 test files):
- `SjukfallController.java` — imports IntygParametrar
- `PopulateFiltersServiceImpl.java` — imports RekoStatusTypeDTO
- `PuService.java`, `PuServiceImpl.java` — imports IntygData
- `CertificateServiceImpl.java` — imports DiagnosKod
- `CreateRekoStatusServiceImpl.java` — imports RekoStatusTypeDTO
- `GetRekoStatusServiceImpl.java` — imports RekoStatusTypeDTO
- `SjukfallServiceImpl.java` — imports IntygData, IntygParametrar, SjukfallEngineService
- `IntygstjanstMapper.java` — imports DiagnosKod, Formaga, IntygData
- `SjukfallEngineMapper.java` — imports RekoStatusTypeDTO
- `RekoStatusDTO.java` (web) — imports RekoStatusTypeDTO
- 9 test files with matching imports

## Todo Breakdown

1. **copy-sjukfall-dto** — Copy 16 DTO files to `common/.../sjukfall/dto/`
2. **copy-sjukfall-engine** — Copy 6 engine files to `common/.../sjukfall/engine/`
3. **copy-sjukfall-services** — Copy 3 service files to `common/.../sjukfall/services/`
4. **copy-sjukfall-util** — Copy 2 util files to `common/.../sjukfall/util/`
5. **update-common-build** — Add `commons-lang3` to `common/build.gradle`
6. **update-sjukfall-config** — Remove infra ComponentScan from `SjukfallConfig.java`
7. **update-imports** — Replace `se.inera.intyg.infra.sjukfall` → `se.inera.intyg.rehabstod.sjukfall` in ~35 files
8. **verify-build** — Run `./gradlew test` and verify all tests pass

## Verification
- `./gradlew test` — all tests pass
- No remaining imports of `se.inera.intyg.infra.sjukfall` in any `.java` file
- Application starts normally (sick leave calculations unchanged)
