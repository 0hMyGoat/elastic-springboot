// ── Config ──────────────────────────────────────────────
const API = '';  // proxié par nginx vers localhost:8080

// ── État global ─────────────────────────────────────────
let allJobs = [];

// ── Navigation ──────────────────────────────────────────
function switchPanel(id, el) {
    document.querySelectorAll('.panel').forEach(p => p.classList.remove('active'));
    document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));
    document.getElementById('panel-' + id).classList.add('active');
    el.classList.add('active');
    const labels = {
        libre: 'Recherche libre', criteres: 'Par critères',
        carte: 'Carte du Québec', secteurs: 'Secteurs', metiers: 'Métiers'
    };
    document.getElementById('topbar-sub').textContent = labels[id] || id;
    // Masquer la stats-bar sur la carte pour maximiser l'espace
    document.querySelector('.stats-bar').style.display = id === 'carte' ? 'none' : '';
    if (id === 'carte') {
        requestAnimationFrame(() => {
            if (!mapInstance) initMap();
            else mapInstance.invalidateSize();
        });
    }
}

// ── Initialisation ──────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
    loadReferentiels();
    loadStats();
    document.getElementById('libre-input').addEventListener('keydown', e => {
        if (e.key === 'Enter') runLibre();
    });
    document.addEventListener('keydown', e => {
        if (e.key === 'Escape') closeDrawer();
    });
});

// ── Référentiels ────────────────────────────────────────
async function loadReferentiels() {
    try {
        const [sectorsData, jobsData] = await Promise.all([
            fetch(`${API}/api/sectors?size=500&sort=name,asc`).then(r => r.json()),
            fetch(`${API}/api/jobs?size=500&sort=name,asc`).then(r => r.json())
        ]);
        const sectors = sectorsData.content || [];
        allJobs = jobsData.content || [];

        // Recherche critères
        const sectorSelect = document.getElementById('c-sector');
        sectors.forEach(s => {
            const opt = document.createElement('option');
            opt.value = s.id;
            opt.textContent = s.name;
            sectorSelect.appendChild(opt);
        });
        populateJobs('');
        sectorSelect.addEventListener('change', () => populateJobs(sectorSelect.value));

        // Carte
        const mapSectorSelect = document.getElementById('map-sector');
        sectors.forEach(s => {
            const opt = document.createElement('option');
            opt.value = s.id;
            opt.textContent = s.name;
            mapSectorSelect.appendChild(opt);
        });
        populateMapJobs('');

        renderSecteurs(sectors);
        renderMetiers(allJobs);
    } catch (e) {
        console.warn('Erreur chargement référentiels', e);
    }
}

function populateJobs(sectorId) {
    const jobSelect = document.getElementById('c-job');
    const currentJobId = jobSelect.value;
    jobSelect.innerHTML = '<option value="">Tous les métiers</option>';
    const filtered = sectorId ? allJobs.filter(j => j.sectorId === sectorId) : allJobs;
    filtered.forEach(j => {
        const opt = document.createElement('option');
        opt.value = j.id;
        opt.textContent = j.name;
        jobSelect.appendChild(opt);
    });
    if (currentJobId && filtered.find(j => j.id === currentJobId)) jobSelect.value = currentJobId;
}

function populateMapJobs(sectorId) {
    const jobSelect = document.getElementById('map-job');
    jobSelect.innerHTML = '<option value="">Tous les métiers</option>';
    const filtered = sectorId ? allJobs.filter(j => j.sectorId === sectorId) : allJobs;
    filtered.forEach(j => {
        const opt = document.createElement('option');
        opt.value = j.id;
        opt.textContent = j.name;
        jobSelect.appendChild(opt);
    });
}

function renderSecteurs(sectors) {
    const container = document.getElementById('results-secteurs');
    if (!sectors.length) {
        container.innerHTML = emptyHtml('Aucun secteur', '');
        return;
    }
    container.innerHTML = `
      <div class="results-bar">
        <span class="results-count"><strong>${sectors.length}</strong> secteur${sectors.length > 1 ? 's' : ''}</span>
      </div>
      <div class="result-list">
        ${sectors.map(s => `
          <div class="result-card">
            <div class="result-info">
              <div class="result-name">${s.name}</div>
              <div class="result-tags"><span class="tag">${s.code}</span></div>
            </div>
          </div>`).join('')}
      </div>`;
}

function renderMetiers(jobs) {
    const container = document.getElementById('results-metiers');
    if (!jobs.length) {
        container.innerHTML = emptyHtml('Aucun métier', '');
        return;
    }
    container.innerHTML = `
      <div class="results-bar">
        <span class="results-count"><strong>${jobs.length}</strong> métier${jobs.length > 1 ? 's' : ''}</span>
      </div>
      <div class="result-list">
        ${jobs.map(j => `
          <div class="result-card">
            <div class="result-info">
              <div class="result-name">${j.name}</div>
              <div class="result-tags">
                <span class="tag">${j.code}</span>
                <span class="tag accent">${j.sectorName}</span>
              </div>
            </div>
          </div>`).join('')}
      </div>`;
}

// ── Stats ───────────────────────────────────────────────
async function loadStats() {
    try {
        const [usersRes, sectorsRes, jobsRes] = await Promise.all([
            fetch(`${API}/api/users/search`, {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({size: 1})
            }).then(r => r.json()),
            fetch(`${API}/api/sectors?size=1`).then(r => r.json()),
            fetch(`${API}/api/jobs?size=1`).then(r => r.json())
        ]);
        document.getElementById('stat-users').textContent = (usersRes.totalElements || 0).toLocaleString('fr-FR');
        document.getElementById('stat-sectors').textContent = sectorsRes.totalElements || 0;
        document.getElementById('stat-jobs').textContent = jobsRes.totalElements || 0;
        ['stat-users', 'stat-sectors', 'stat-jobs'].forEach(id => document.getElementById(id).classList.remove('loading'));
    } catch (e) {
        ['stat-users', 'stat-sectors', 'stat-jobs'].forEach(id => document.getElementById(id).textContent = '—');
    }
}

// ── Recherche libre ─────────────────────────────────────
async function runLibre(page = 0) {
    const q = document.getElementById('libre-input').value.trim();
    if (!q) return;
    const btn = document.getElementById('btn-libre');
    btn.disabled = true;
    setLoading('results-libre');
    try {
        const data = await fetch(`${API}/api/users/search?query=${encodeURIComponent(q)}&page=${page}&size=20`).then(r => r.json());
        renderResults(data, 'results-libre', p => runLibre(p));
    } catch (e) {
        setError('results-libre');
    } finally {
        btn.disabled = false;
    }
}

// ── Recherche par critères ───────────────────────────────
async function runCriteres(page = 0) {
    const body = {page, size: 20};
    const fn = document.getElementById('c-fn').value.trim();
    const ln = document.getElementById('c-ln').value.trim();
    const city = document.getElementById('c-city').value.trim();
    const postal = document.getElementById('c-postal').value.trim();
    const sectorId = document.getElementById('c-sector').value;
    const jobId = document.getElementById('c-job').value;
    if (fn) body.firstName = fn;
    if (ln) body.lastName = ln;
    if (city) body.city = city;
    if (postal) body.postalCode = postal;
    if (sectorId) body.sectorId = sectorId;
    if (jobId) body.jobId = jobId;
    const btn = document.getElementById('btn-criteres');
    btn.disabled = true;
    setLoading('results-criteres');
    try {
        const data = await fetch(`${API}/api/users/search`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(body)
        }).then(r => r.json());
        renderResults(data, 'results-criteres', p => runCriteres(p));
    } catch (e) {
        setError('results-criteres');
    } finally {
        btn.disabled = false;
    }
}

function resetCriteres() {
    ['c-fn', 'c-ln', 'c-city', 'c-postal'].forEach(id => document.getElementById(id).value = '');
    document.getElementById('c-sector').value = '';
    populateJobs('');
    document.getElementById('results-criteres').innerHTML = emptyHtml('Définissez vos critères', 'Renseignez au moins un filtre puis lancez la recherche.');
}

// ── Rendu des résultats ─────────────────────────────────
function renderResults(page, containerId, onPageChange) {
    const container = document.getElementById(containerId);
    const users = page.content || [];
    const total = page.totalElements || 0;
    const totalPages = page.totalPages || 0;
    const currentPage = page.number || 0;
    if (!users.length) {
        container.innerHTML = emptyHtml('Aucun résultat', "Essayez d'élargir vos critères.");
        return;
    }

    let html = `
      <div class="results-bar">
        <span class="results-count"><strong>${total.toLocaleString('fr-FR')}</strong> utilisateur${total > 1 ? 's' : ''} trouvé${total > 1 ? 's' : ''}</span>
      </div>
      <div class="result-list">`;

    users.forEach(u => {
        const initials = (u.firstName?.[0] || '') + (u.lastName?.[0] || '');
        const email = u.emails?.[0] || '';
        const phone = u.phones?.[0] || '';
        html += `
        <div class="result-card" onclick="openDrawer('${u.id}')">
          <div class="avatar">${initials}</div>
          <div class="result-info">
            <div class="result-name">${u.firstName || ''} ${u.lastName || ''}</div>
            <div class="result-tags">
              ${u.city ? `<span class="tag">${formatLabel(u.city)}</span>` : ''}
              ${u.sectorName ? `<span class="tag accent">${u.sectorName}</span>` : ''}
              ${u.jobName ? `<span class="tag">${u.jobName}</span>` : ''}
            </div>
          </div>
          <div class="result-contact">
            ${email ? `<div class="result-email">${email}</div>` : ''}
            ${phone ? `<div class="result-phone">${phone}</div>` : ''}
          </div>
          <svg class="chevron-right" width="14" height="14" viewBox="0 0 14 14" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="m5 2 4 5-4 5"/>
          </svg>
        </div>`;
    });

    html += `</div>` + buildPagination(currentPage, totalPages, onPageChange);
    container.innerHTML = html;
}

function buildPagination(current, total, onPageChange) {
    if (total <= 1) return '';
    let pages = [];
    for (let i = 0; i < total; i++) {
        if (i === 0 || i === total - 1 || (i >= current - 2 && i <= current + 2)) pages.push(i);
    }
    pages = [...new Set(pages)].sort((a, b) => a - b);
    let html = `<div class="pagination">
      <button class="page-btn" ${current === 0 ? 'disabled' : ''} onclick="(${onPageChange.toString()})(${current - 1})">
        <svg width="12" height="12" viewBox="0 0 12 12" fill="none" stroke="currentColor" stroke-width="1.8"><path d="m8 2-4 4 4 4"/></svg>
      </button>`;
    let last = -1;
    pages.forEach(p => {
        if (last !== -1 && p > last + 1) html += `<span style="color:var(--text-tertiary);padding:0 4px;">…</span>`;
        html += `<button class="page-btn ${p === current ? 'active' : ''}" onclick="(${onPageChange.toString()})(${p})">${p + 1}</button>`;
        last = p;
    });
    html += `<button class="page-btn" ${current >= total - 1 ? 'disabled' : ''} onclick="(${onPageChange.toString()})(${current + 1})">
        <svg width="12" height="12" viewBox="0 0 12 12" fill="none" stroke="currentColor" stroke-width="1.8"><path d="m4 2 4 4-4 4"/></svg>
      </button></div>`;
    return html;
}

// ── Carte Leaflet ────────────────────────────────────────
let mapInstance = null;
let mapMarkers = [];
let mapDebounceTimer = null;
let mapJobId = '';
let mapSectorId = '';

// zoom ≥ 14 → INDIVIDUAL (mode géohash pour zoom < 14)

/**
 * Seuil minimum de personnes par cluster pour affichage.
 * Évite la pollution visuelle aux vues panoramiques.
 */
function getMinCount(zoom) {
    if (zoom <= 6) return 100;  // province entière → grandes villes seulement
    if (zoom <= 8) return 20;   // région → villes moyennes
    if (zoom <= 10) return 5;    // agglo → tous les bourgs notables
    return 1;                    // geohash fine / INDIVIDUAL → tout
}

function initMap() {
    if (mapInstance) return;

    mapInstance = L.map('leaflet-map', {zoomControl: true, minZoom: 5, maxZoom: 18})
        .setView([53.0, -72.0], 6);   // Centre du Québec

    L.tileLayer('https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OSM</a> &copy; <a href="https://carto.com/">CARTO</a>',
        subdomains: 'abcd',
        maxZoom: 19
    }).addTo(mapInstance);

    mapInstance.on('moveend zoomend', () => {
        clearTimeout(mapDebounceTimer);
        mapDebounceTimer = setTimeout(loadMapClusters, 450);
    });

    // Fermer le side panel si on clique sur la carte
    mapInstance.on('click', () => closeMapSidePanel());

    // Filtres carte
    document.getElementById('map-sector').addEventListener('change', function () {
        mapSectorId = this.value;
        populateMapJobs(this.value);
        mapJobId = '';
        loadMapClusters();
    });
    document.getElementById('map-job').addEventListener('change', function () {
        mapJobId = this.value;
        loadMapClusters();
    });

    loadMapClusters();
}

async function loadMapClusters() {
    if (!mapInstance) return;

    const zoom = mapInstance.getZoom();
    const label = zoom >= 14 ? 'Individuel' : 'Regroupé par cellule';
    document.getElementById('map-precision-label').textContent = label;

    const bounds = mapInstance.getBounds();
    let url = `${API}/api/users/map/clusters?zoomLevel=${zoom}`;
    if (mapJobId) url += `&jobId=${mapJobId}`;
    if (mapSectorId) url += `&sectorId=${mapSectorId}`;
    if (zoom >= 14) {
        url += `&minLat=${bounds.getSouth()}&maxLat=${bounds.getNorth()}`
            + `&minLon=${bounds.getWest()}&maxLon=${bounds.getEast()}`;
    }

    try {
        const points = await fetch(url).then(r => r.json());
        renderMapPoints(points, zoom);
    } catch (e) {
        console.error('[Map] Erreur chargement clusters', e);
    }
}

function renderMapPoints(points, zoom) {
    mapMarkers.forEach(m => mapInstance.removeLayer(m));
    mapMarkers = [];

    const minCount = getMinCount(zoom);

    points.forEach(p => {
        // ── Filtrage côté client : masquer les micro-clusters selon le zoom ──
        if (zoom < 14 && p.count < minCount) return;

        let marker;
        if (zoom >= 14) {
            const icon = L.divIcon({
                className: 'map-individual-icon',
                html: `<div class="map-individual-dot"></div>`,
                iconSize: [12, 12],
                iconAnchor: [6, 6]
            });
            marker = L.marker([p.lat, p.lon], {icon});
            marker.bindTooltip(formatLabel(p.label), {direction: 'top', offset: [0, -8]});
            marker.on('click', e => {
                L.DomEvent.stopPropagation(e);
                openDrawer(p.userId);
            });
        } else {
            // Échelle log : différencie bien les petites/grandes villes
            // count=5 → r=14  count=100 → r=23  count=2000 → r=33  count=10000 → r=38
            const r = Math.round(Math.min(9 + Math.log1p(p.count) * 3.5, 38));
            const fontSize = Math.max(9, Math.min(13, r / 2.4)).toFixed(0);
            const labelTxt = p.count >= 10000 ? `${(p.count / 1000).toFixed(0)}k`
                : p.count >= 1000 ? `${(p.count / 1000).toFixed(1)}k`
                    : p.count;
            const icon = L.divIcon({
                className: 'map-cluster-icon',
                html: `<div class="map-cluster-inner" style="width:${r * 2}px;height:${r * 2}px;">
                   <span style="font-size:${fontSize}px;">${labelTxt}</span>
                 </div>`,
                iconSize: [r * 2, r * 2],
                iconAnchor: [r, r]
            });
            marker = L.marker([p.lat, p.lon], {icon});
            const tooltip = `<strong>${formatLabel(p.label)}</strong><br>${p.count.toLocaleString('fr-FR')} personne${p.count > 1 ? 's' : ''}`;
            marker.bindTooltip(tooltip, {direction: 'top', offset: [0, -r - 4]});
            marker.on('click', e => {
                L.DomEvent.stopPropagation(e);
                loadClusterUsers(p.label, p.label, formatLabel(p.label), p.count);
            });
        }
        marker.addTo(mapInstance);
        mapMarkers.push(marker);
    });
}

// ── Panneau latéral (liste des membres d'un cluster) ─────
async function loadClusterUsers(geohashCell, rawValue, displayLabel, count) {
    openMapSidePanel(displayLabel, count);
    document.getElementById('map-side-content').innerHTML =
        `<div class="loading-state"><div class="spinner"></div><p>Chargement…</p></div>`;

    let url = `${API}/api/users/map/cluster-users?geohashCell=${encodeURIComponent(geohashCell)}`;
    if (mapJobId) url += `&jobId=${mapJobId}`;
    if (mapSectorId) url += `&sectorId=${mapSectorId}`;

    try {
        const users = await fetch(url).then(r => r.json());
        renderMapSidePanel(users);
    } catch (e) {
        document.getElementById('map-side-content').innerHTML =
            `<div class="error-state">Impossible de charger les utilisateurs.</div>`;
    }
}

function renderMapSidePanel(users) {
    if (!users.length) {
        document.getElementById('map-side-content').innerHTML = emptyHtml('Aucun résultat', '');
        return;
    }
    let html = '';
    users.forEach(u => {
        const initials = (u.firstName?.[0] || '') + (u.lastName?.[0] || '');
        html += `
        <div class="result-card" onclick="openDrawer('${u.id}')">
          <div class="avatar">${initials}</div>
          <div class="result-info">
            <div class="result-name">${u.firstName || ''} ${u.lastName || ''}</div>
            <div class="result-tags">
              ${u.sectorName ? `<span class="tag accent">${u.sectorName}</span>` : ''}
              ${u.jobName ? `<span class="tag">${u.jobName}</span>` : ''}
            </div>
          </div>
          <svg class="chevron-right" width="14" height="14" viewBox="0 0 14 14" fill="none" stroke="currentColor" stroke-width="1.5"><path d="m5 2 4 5-4 5"/></svg>
        </div>`;
    });
    document.getElementById('map-side-content').innerHTML = html;
}

function openMapSidePanel(label, count) {
    document.getElementById('map-side-title').textContent = label || '—';
    document.getElementById('map-side-count').textContent = count != null ? `${count} pers.` : '';
    document.getElementById('map-side-panel').classList.add('open');
    setTimeout(() => mapInstance && mapInstance.invalidateSize(), 280);
}

function closeMapSidePanel() {
    document.getElementById('map-side-panel').classList.remove('open');
    setTimeout(() => mapInstance && mapInstance.invalidateSize(), 280);
}

function resetMapFilters() {
    document.getElementById('map-sector').value = '';
    document.getElementById('map-job').value = '';
    mapSectorId = '';
    mapJobId = '';
    populateMapJobs('');
    loadMapClusters();
}

// ── Drawer détail utilisateur ────────────────────────────
async function openDrawer(userId) {
    document.getElementById('drawer-overlay').classList.add('open');
    document.getElementById('user-drawer').classList.add('open');
    document.body.style.overflow = 'hidden';
    document.getElementById('drawer-header').innerHTML =
        `<div class="drawer-spinner"><div class="spinner"></div></div>`;
    document.getElementById('drawer-body').innerHTML = '';
    try {
        const u = await fetch(`${API}/api/users/${userId}`).then(r => r.json());
        renderDrawer(u);
    } catch (e) {
        document.getElementById('drawer-header').innerHTML =
            `<div class="error-state" style="margin:0">Impossible de charger le détail.</div>`;
    }
}

function closeDrawer() {
    document.getElementById('drawer-overlay').classList.remove('open');
    document.getElementById('user-drawer').classList.remove('open');
    document.body.style.overflow = '';
}

function renderDrawer(u) {
    const initials = (u.firstName?.[0] || '') + (u.lastName?.[0] || '');
    const birth = u.birthDate
        ? new Date(u.birthDate).toLocaleDateString('fr-FR', {day: 'numeric', month: 'long', year: 'numeric'})
        : null;
    const registered = u.registeredAt
        ? new Date(u.registeredAt).toLocaleDateString('fr-FR', {day: 'numeric', month: 'long', year: 'numeric'})
        : null;

    document.getElementById('drawer-header').innerHTML = `
      <div class="drawer-avatar">${initials}</div>
      <div class="drawer-identity">
        <div class="drawer-name">${u.firstName} ${u.lastName}</div>
        <div class="drawer-meta">
          ${birth ? `Né·e le ${birth}` : ''}
          ${birth && registered ? '<br>' : ''}
          ${registered ? `Inscrit·e le ${registered}` : ''}
        </div>
      </div>
      <button class="drawer-close" onclick="closeDrawer()">
        <svg width="18" height="18" viewBox="0 0 18 18" fill="none" stroke="currentColor" stroke-width="1.8">
          <path d="M4 4l10 10M14 4L4 14"/>
        </svg>
      </button>`;

    let body = '';

    const jobs = [...(u.userJobs || [])].sort((a, b) => (b.primaryJob ? 1 : 0) - (a.primaryJob ? 1 : 0));
    if (jobs.length) {
        body += `<div><p class="drawer-section-title">Emplois (${jobs.length})</p>`;
        jobs.forEach(j => {
            const fmt = d => d ? new Date(d).toLocaleDateString('fr-FR', {month: 'short', year: 'numeric'}) : null;
            const start = fmt(j.startDate);
            const end = j.endDate ? fmt(j.endDate) : "aujourd'hui";
            const dateStr = start ? `${start} → ${end}` : null;
            body += `
          <div class="drawer-job">
            <div class="drawer-job-dot ${j.primaryJob ? 'primary' : 'secondary'}"></div>
            <div>
              <div class="drawer-job-name">
                ${j.jobName}
                ${j.primaryJob ? '<span style="font-size:10px;color:var(--accent-text);margin-left:6px;">★ Principal</span>' : ''}
              </div>
              <div class="drawer-job-sector">${j.sectorName}</div>
              ${dateStr ? `<div class="drawer-job-dates">${dateStr}</div>` : ''}
            </div>
          </div>`;
        });
        body += `</div>`;
    }

    const contacts = u.contactInfos || [];
    if (contacts.length) {
        body += `<div><p class="drawer-section-title">Coordonnées (${contacts.length} fiche${contacts.length > 1 ? 's' : ''})</p>`;
        contacts.forEach(c => {
            body += `<div class="drawer-contact-group">
          <div class="drawer-contact-type">${formatContactType(c.type)}</div>
          ${c.label ? `<div class="drawer-contact-label">${c.label}</div>` : ''}`;
            (c.addresses || []).forEach(a => {
                const lines = [a.streetLine1, a.streetLine2, [a.city, a.postalCode].filter(Boolean).join(' '), a.state, a.country].filter(Boolean);
                body += `<div class="drawer-contact-row">
            <svg width="13" height="13" viewBox="0 0 13 13" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M6.5 1C4.57 1 3 2.57 3 4.5c0 2.84 3.5 7.5 3.5 7.5S10 7.34 10 4.5C10 2.57 8.43 1 6.5 1z"/>
              <circle cx="6.5" cy="4.5" r="1.3"/>
            </svg>
            <span>${lines.join(', ')}</span>
          </div>`;
            });
            (c.emails || []).forEach(e => {
                body += `<div class="drawer-contact-row">
            <svg width="13" height="13" viewBox="0 0 13 13" fill="none" stroke="currentColor" stroke-width="1.5">
              <rect x="1" y="3" width="11" height="7" rx="1"/>
              <path d="M1 3.5l5.5 3.5L12 3.5"/>
            </svg>
            <span style="color:var(--info-text)">${e.email}</span>
          </div>`;
            });
            (c.phones || []).forEach(p => {
                body += `<div class="drawer-contact-row">
            <svg width="13" height="13" viewBox="0 0 13 13" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M2 1.5h2.5L6 4.5 4.5 5.5C5.3 7.1 6.9 8.7 8.5 9.5l1-1.5 3 1.5V12c0 .28-.22.5-.5.5C5.4 12.5.5 7.6.5 1.5c0-.28.22-.5.5-.5z"/>
            </svg>
            <span>${p.number}${p.type ? `<span style="font-size:11px;color:var(--text-tertiary);margin-left:5px;">(${formatPhoneType(p.type)})</span>` : ''}</span>
          </div>`;
            });
            body += `</div>`;
        });
        body += `</div>`;
    }

    document.getElementById('drawer-body').innerHTML =
        body || `<p style="color:var(--text-tertiary);font-size:13px;">Aucune information disponible.</p>`;
}

function formatContactType(type) {
    const map = {
        WORK_PRIMARY: 'Pro. principal',
        WORK_SECONDARY: 'Pro. secondaire',
        PERSONAL: 'Personnel',
        OTHER: 'Autre'
    };
    return map[type] || type;
}

function formatPhoneType(type) {
    const map = {MOBILE: 'Mobile', HOME: 'Domicile', WORK: 'Bureau', FAX: 'Fax'};
    return map[type] || type;
}

// ── Helpers ─────────────────────────────────────────────
/** Capitalise les mots séparés par espace ou tiret */
function formatLabel(str) {
    if (!str) return str;
    return str.replace(/(^|[\s-])(\S)/g, (m, sep, c) => sep + c.toUpperCase());
}

function setLoading(containerId) {
    document.getElementById(containerId).innerHTML =
        `<div class="loading-state"><div class="spinner"></div><p>Recherche en cours…</p></div>`;
}

function setError(containerId) {
    document.getElementById(containerId).innerHTML =
        `<div class="error-state">Impossible de contacter l'API. Vérifiez que Spring Boot est démarré sur le port 8080.</div>`;
}

function emptyHtml(title, sub) {
    return `<div class="empty-state">
      <div class="empty-icon">
        <svg width="22" height="22" viewBox="0 0 22 22" fill="none" stroke="currentColor" stroke-width="1.5" style="color:var(--text-tertiary)">
          <circle cx="9" cy="9" r="7"/><path d="m16 16 4 4"/>
        </svg>
      </div>
      <p class="empty-title">${title}</p>
      ${sub ? `<p class="empty-sub">${sub}</p>` : ''}
    </div>`;
}