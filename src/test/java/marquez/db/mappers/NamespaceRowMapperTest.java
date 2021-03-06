/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package marquez.db.mappers;

import static marquez.common.models.CommonModelGenerator.newDescription;
import static marquez.common.models.CommonModelGenerator.newNamespaceName;
import static marquez.common.models.CommonModelGenerator.newOwnerName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;
import marquez.UnitTests;
import marquez.common.models.Description;
import marquez.common.models.NamespaceName;
import marquez.common.models.OwnerName;
import marquez.db.Columns;
import marquez.db.models.NamespaceRow;
import org.jdbi.v3.core.statement.StatementContext;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

@Category(UnitTests.class)
public class NamespaceRowMapperTest {
  private static final UUID ROW_UUID = UUID.randomUUID();
  private static final Instant CREATED_AT = Instant.now();
  private static final NamespaceName NAME = newNamespaceName();
  private static final Description DESCRIPTION = newDescription();
  private static final OwnerName CURRENT_OWNER_NAME = newOwnerName();

  @Rule public MockitoRule rule = MockitoJUnit.rule();

  @Mock private Object exists;
  @Mock private ResultSet results;
  @Mock private StatementContext context;

  @Test
  public void testMap_row() throws SQLException {
    when(results.getObject(Columns.ROW_UUID)).thenReturn(exists);
    when(results.getObject(Columns.CREATED_AT)).thenReturn(exists);
    when(results.getObject(Columns.NAME)).thenReturn(exists);
    when(results.getObject(Columns.DESCRIPTION)).thenReturn(exists);
    when(results.getObject(Columns.CURRENT_OWNER_NAME)).thenReturn(exists);

    when(results.getObject(Columns.ROW_UUID, UUID.class)).thenReturn(ROW_UUID);
    when(results.getTimestamp(Columns.CREATED_AT)).thenReturn(Timestamp.from(CREATED_AT));
    when(results.getString(Columns.NAME)).thenReturn(NAME.getValue());
    when(results.getString(Columns.DESCRIPTION)).thenReturn(DESCRIPTION.getValue());
    when(results.getString(Columns.CURRENT_OWNER_NAME)).thenReturn(CURRENT_OWNER_NAME.getValue());

    final NamespaceRowMapper rowMapper = new NamespaceRowMapper();
    final NamespaceRow row = rowMapper.map(results, context);
    assertThat(row.getUuid()).isEqualTo(ROW_UUID);
    assertThat(row.getCreatedAt()).isEqualTo(CREATED_AT);
    assertThat(row.getName()).isEqualTo(NAME.getValue());
    assertThat(row.getDescription()).isEqualTo(DESCRIPTION.getValue());
    assertThat(row.getCurrentOwnerName()).isEqualTo(CURRENT_OWNER_NAME.getValue());
  }

  @Test
  public void testMap_row_noDescription() throws SQLException {
    when(results.getObject(Columns.ROW_UUID)).thenReturn(exists);
    when(results.getObject(Columns.CREATED_AT)).thenReturn(exists);
    when(results.getObject(Columns.NAME)).thenReturn(exists);
    when(results.getObject(Columns.DESCRIPTION)).thenReturn(null);
    when(results.getObject(Columns.CURRENT_OWNER_NAME)).thenReturn(exists);

    when(results.getObject(Columns.ROW_UUID, UUID.class)).thenReturn(ROW_UUID);
    when(results.getTimestamp(Columns.CREATED_AT)).thenReturn(Timestamp.from(CREATED_AT));
    when(results.getString(Columns.NAME)).thenReturn(NAME.getValue());
    when(results.getString(Columns.CURRENT_OWNER_NAME)).thenReturn(CURRENT_OWNER_NAME.getValue());

    final NamespaceRowMapper rowMapper = new NamespaceRowMapper();
    final NamespaceRow row = rowMapper.map(results, context);
    assertThat(row.getUuid()).isEqualTo(ROW_UUID);
    assertThat(row.getCreatedAt()).isEqualTo(CREATED_AT);
    assertThat(row.getName()).isEqualTo(NAME.getValue());
    assertThat(row.getDescription()).isNull();
    assertThat(row.getCurrentOwnerName()).isEqualTo(CURRENT_OWNER_NAME.getValue());
  }

  @Test
  public void testMap_throwsException_onNullResults() throws SQLException {
    final ResultSet nullResults = null;
    final NamespaceRowMapper rowMapper = new NamespaceRowMapper();
    assertThatNullPointerException().isThrownBy(() -> rowMapper.map(nullResults, context));
  }

  @Test
  public void testMap_throwsException_onNullContext() throws SQLException {
    final StatementContext nullContext = null;
    final NamespaceRowMapper rowMapper = new NamespaceRowMapper();
    assertThatNullPointerException().isThrownBy(() -> rowMapper.map(results, nullContext));
  }
}
