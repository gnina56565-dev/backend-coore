@import java.util.List
@import ru.mentee.power.crm.model.Lead
@import ru.mentee.power.crm.model.LeadStatus

@template.servlet.layout.main(content = @`
<div class="mb-4 flex gap-2">
  <a href="/leads"
     class="px-4 py-2 rounded ${currentFilter == null ? 'bg-blue-500 text-white' : 'bg-gray-200'}">
    Все
  </a>
  <a href="/leads?status=NEW"
     class="px-4 py-2 rounded ${currentFilter == LeadStatus.NEW ? 'bg-blue-500 text-white' : 'bg-gray-200'}">
    NEW
  </a>
  <a href="/leads?status=CONTACTED"
     class="px-4 py-2 rounded ${currentFilter == LeadStatus.CONTACTED ? 'bg-blue-500 text-white' : 'bg-gray-200'}">
    CONTACTED
  </a>
  <a href="/leads?status=QUALIFIED"
     class="px-4 py-2 rounded ${currentFilter == LeadStatus.QUALIFIED ? 'bg-blue-500 text-white' : 'bg-gray-200'}">
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
                <th class="px-4 py-2 text-left">Phone</th>
                <th class="px-4 py-2 text-left">Company</th>
            </tr>
        </thead>
        <tbody>
            @for(var lead : leads)
                <tr class="border-t hover:bg-gray-50">
                    <td class="px-4 py-2 font-mono text-sm">${lead.id()}</td>
                    <td class="px-4 py-2">${lead.contact().email()}</td>
                    <td class="px-4 py-2">${lead.contact().phone()}</td>
                    <td class="px-4 py-2 font-semibold">${lead.company()}</td>
                </tr>
            @endfor
        </tbody>
    </table>
</div>
`)