@import java.util.List
@import ru.mentee.power.crm.model.Lead
@import ru.mentee.power.crm.model.LeadStatus
@param String search
@param List<Lead> leads
@param LeadStatus currentFilter
@template.layout.main(content = @`
<div class="container mx-auto p-6">
  <!-- Форма поиска (без изменений) -->
  <form method="get" action="/leads" class="bg-white shadow rounded-lg p-4 mb-6">
        <input type="text" name="search" value="${search}">
        <select name="status">
            <option value="">Все</option>
            <option value="NEW" selected="${currentFilter == LeadStatus.NEW}">NEW</option>
            <option value="CONTACTED" selected="${currentFilter == LeadStatus.CONTACTED}">CONTACTED</option>
            <option value="QUALIFIED" selected="${currentFilter == LeadStatus.QUALIFIED}">QUALIFIED</option>
        </select>
        <button type = "submit">Найти</button>
        <a href="/leads">Сбросить</a>
  </form>

  <!-- Фильтры (без изменений) -->
  <div class="mb-4 flex gap-2">
    <!-- ... ваши существующие кнопки фильтров ... -->
     <a href="/leads" class="px-4 py-2 rounded ${currentFilter == null ? "bg-blue-500 text-white" : "bg-gray-200"}">Все</a>
     <a href="/leads?status=NEW" class="px-4 py-2 rounded ${currentFilter == LeadStatus.NEW ? "bg-blue-500 text-white" : "bg-gray-200"}">NEW</a>
     <a href="/leads?status=CONTACTED" class="px-4 py-2 rounded ${currentFilter == LeadStatus.CONTACTED ? "bg-blue-500 text-white" : "bg-gray-200"}">CONTACTED</a>
     <a href="/leads?status=QUALIFIED" class="px-4 py-2 rounded ${currentFilter == LeadStatus.QUALIFIED ? "bg-blue-500 text-white" : "bg-gray-200"}">QUALIFIED</a>
  </div>

  @if(currentFilter != null)
    <p class="text-sm text-gray-600 mb-2">Показаны лиды со статусом: ${currentFilter}</p>
  @endif

  <div class="bg-white rounded-lg shadow-md p-6">
      <h2 class="text-2xl font-bold mb-4">Lead List (Spring Boot)</h2>

      <table class="min-w-full bg-white border border-gray-200">
          <thead class="bg-gray-100">
              <tr>
                  <th class="px-4 py-2 text-left">ID</th>
                  <th class="px-4 py-2 text-left">Email</th>
                  <th class="px-4 py-2 text-left">Company</th>
                  <th class="px-4 py-2 text-left">Status</th>
                  <!-- ДОБАВЛЕНО: Заголовок для новой колонки -->
                  <th class="px-4 py-2 text-left">Сделки</th>
                  <th class="px-4 py-2 text-left">Действия</th>
              </tr>
          </thead>
          <tbody>
              @for(var lead : leads)
                  <tr class="border-t hover:bg-gray-50">
                      <td class="px-4 py-2 font-mono text-sm">${lead.getId() != null ? lead.getId().toString() : "-"}</td>
                      <td class="px-4 py-2">${lead.getEmail() != null ? lead.getEmail() : "-"}</td>
                      <td class="px-4 py-2 font-semibold">${lead.getCompany() != null ? lead.getCompany().getName() : "-"}</td>
                      <td class="px-4 py-2">
                          <span class="px-2 py-1 rounded text-xs font-medium ${lead.getStatus() == LeadStatus.NEW ? "bg-blue-100 text-blue-800" : lead.getStatus() == LeadStatus.CONTACTED ? "bg-yellow-100 text-yellow-800" : "bg-green-100 text-green-800"}">
                              ${lead.getStatus()}
                          </span>
                      </td>

                      <!-- ДОБАВЛЕНО: Ячейка с кнопкой создания сделки -->
                      <td class="px-4 py-2">
                          @if(lead.getStatus() == ru.mentee.power.crm.model.LeadStatus.NEW || lead.getStatus() == ru.mentee.power.crm.model.LeadStatus.CONTACTED)
                              <a href="/deals/convert/${lead.getId().toString()}"
                                 class="text-green-600 hover:text-green-800 hover:underline font-semibold text-sm">
                                 + Создать сделку
                              </a>
                          @else
                              <span class="text-gray-400 text-sm italic">Конвертирован</span>
                          @endif
                      </td>

                      <td class="px-4 py-2">
                          <a href="/leads/${lead.getId().toString()}/edit" class="text-blue-600 hover:underline font-medium text-sm">
                              Редактировать
                          </a>
                          <form method="post" action="/leads/${lead.getId().toString()}/delete" class="inline"
                                onclick="return confirm('Удалить лида ${lead.getCompany() != null ? lead.getCompany().getName() : "-"}?')">
                            <button type="submit" class="bg-red-600 text-white px-3 py-1 rounded hover:bg-red-700 text-sm ml-2">
                              Удалить
                            </button>
                          </form>
                      </td>
                  </tr>
              @endfor
          </tbody>
      </table>
  </div>
</div>
`)