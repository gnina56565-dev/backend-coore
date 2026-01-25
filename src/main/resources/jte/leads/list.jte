@param java.util.List<ru.mentee.power.crm.domain.Lead> leads
@template.layout.main(content = @`
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
