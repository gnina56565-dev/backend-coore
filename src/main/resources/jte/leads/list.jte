@import java.util.List
@import ru.mentee.power.crm.model.Lead
@import ru.mentee.power.crm.model.LeadStatus

@param List<Lead> leads
@param LeadStatus currentFilter = null

@template.layout.main(content = @`
<div class="mb-4 flex gap-2">
  <a href="/leads"
     class="px-4 py-2 rounded ${currentFilter == null ? "bg-pink-500 text-white" : "bg-gray-200"}">
    Все
  </a>
  <a href="/leads?status=NEW"
     class="px-4 py-2 rounded ${currentFilter == LeadStatus.NEW ? "bg-pink-500 text-white" : "bg-gray-200"}">
    NEW
  </a>
  <a href="/leads?status=CONTACTED"
     class="px-4 py-2 rounded ${currentFilter == LeadStatus.CONTACTED ? "bg-pink-500 text-white" : "bg-gray-200"}">
    CONTACTED
  </a>
  <a href="/leads?status=QUALIFIED"
     class="px-4 py-2 rounded ${currentFilter == LeadStatus.QUALIFIED ? "bg-pink-500 text-white" : "bg-gray-200"}">
    QUALIFIED
  </a>
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
                <th class="px-4 py-2 text-left">Действия</th>  <!-- ДОБАВЛЕНО -->
            </tr>
        </thead>
        <tbody>
            @for(Lead lead : leads)
                <tr class="border-t hover:bg-gray-50">
                    <td class="px-4 py-2 font-mono text-sm">${lead.getId() != null ? lead.getId().toString() : "-"}</td>
                    <td class="px-4 py-2">${lead.getEmail()}</td>
                    <td class="px-4 py-2 font-semibold">${lead.getCompany() != null ? lead.getCompany().getName() : "-"}</td>
                    <td class="px-4 py-2">
                        <span class="px-2 py-1 rounded text-xs font-medium ${lead.getStatus() == LeadStatus.NEW ? "bg-blue-100 text-blue-800" : lead.getStatus() == LeadStatus.CONTACTED ? "bg-yellow-100 text-yellow-800" : "bg-green-100 text-green-800"}">
                            ${lead.getStatus()}
                        </span>
                    </td>
                    <!-- ДОБАВЛЕНО -->
                    <td class="px-4 py-2">
                        <a href="/servlet/leads/${lead.getId().toString()}/edit" class="text-blue-600 hover:underline font-medium">
                            Редактировать
                        </a>
                    </td>
                </tr>
            @endfor
        </tbody>
    </table>
</div>
`)