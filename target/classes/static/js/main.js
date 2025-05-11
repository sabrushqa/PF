// État global du CV
let cvData = {
  personalInfo: {},
  summary: '',
  experience: [],
  education: [],
  skills: [],
  languages: [],
  themeColor: '#1e88e5',
  fontFamily: 'Roboto'
};

// Initialize emoji picker
const picker = document.querySelector('emoji-picker');
let currentInput = null;
let currentTrigger = null;

document.querySelectorAll('.emoji-trigger').forEach(trigger => {
  trigger.addEventListener('click', (e) => {
    const rect = e.target.getBoundingClientRect();
    picker.style.display = 'block';
    picker.style.left = `${rect.left}px`;
    picker.style.top = `${rect.bottom + 5}px`;
    currentInput = e.target.closest('.input-with-emoji').querySelector('input, textarea');
    currentTrigger = e.target;
  });
});

// Handle emoji selection
picker.addEventListener('emoji-click', event => {
  if (currentInput && currentTrigger) {
    const start = currentInput.selectionStart;
    const end = currentInput.selectionEnd;
    const text = currentInput.value;
    const before = text.substring(0, start);
    const after = text.substring(end);
    currentInput.value = before + event.detail.unicode + after;
    currentTrigger.textContent = event.detail.unicode;
    currentInput.dispatchEvent(new Event('input'));
  }
  picker.style.display = 'none';
});

// Close emoji picker when clicking outside
document.addEventListener('click', (e) => {
  if (!e.target.closest('emoji-picker') && !e.target.closest('.emoji-trigger')) {
    picker.style.display = 'none';
  }
});

// Reset emoji to default when input is cleared
document.addEventListener('input', (e) => {
  if (e.target.tagName === 'INPUT' || e.target.tagName === 'TEXTAREA') {
    const emojiTrigger = e.target.closest('.input-with-emoji')?.querySelector('.emoji-trigger');
    if (emojiTrigger && !e.target.value) {
      const defaultEmoji = emojiTrigger.dataset.default;
      if (defaultEmoji) {
        emojiTrigger.textContent = defaultEmoji;
      }
    }
  }
});

// Gestionnaire de formulaire
document.getElementById('cvForm').addEventListener('input', (e) => {
  updateCVData(e.target);
  updatePreview();
});

// Mise à jour des données du CV
function updateCVData(input) {
  const id = input.id;

  if (id === 'themeColor') {
    cvData.themeColor = input.value;
    updateThemeColor(input.value);
  } else if (id === 'photo') {
    handlePhotoUpload(input);
  } else if (id === 'fontFamily') {
    cvData.fontFamily = input.value;
    updateFont(input.value);
  } else {
    const section = input.closest('.form-group').querySelector('h2').textContent.toLowerCase();
    if (section.includes('personnelles')) {
      cvData.personalInfo[id] = input.value;
    } else if (section.includes('résumé')) {
      cvData.summary = input.value;
    }
  }
}

// Update font family
window.updateFont = (fontFamily) => {
  document.documentElement.style.setProperty('--font-family', `'${fontFamily}', sans-serif`);
  cvData.fontFamily = fontFamily;
  updatePreview();
};

// Gestion de l'upload de photo
function handlePhotoUpload(input) {
  const file = input.files[0];
  if (file) {
    const reader = new FileReader();
    reader.onload = (e) => {
      cvData.personalInfo.photo = e.target.result;
      updatePreview();
    };
    reader.readAsDataURL(file);
  }
}

// Mise à jour de la couleur du thème
function updateThemeColor(color) {
  document.documentElement.style.setProperty('--primary-color', color);
}

// Ajout d'une compétence
window.addSkill = () => {
  const container = document.getElementById('skillsContainer');
  const entry = document.createElement('div');
  entry.className = 'skills-entry';
  entry.innerHTML = `
    <div class="input-with-emoji">
      <input type="text" placeholder="Compétence">
      <button type="button" class="emoji-trigger" data-target="skill" data-default="⭐">⭐</button>
    </div>
    <select>
      <option value="expert">Expert</option>
      <option value="avance">Avancé</option>
      <option value="intermediaire">Intermédiaire</option>
      <option value="debutant">Débutant</option>
    </select>
  `;
  container.insertBefore(entry, container.lastElementChild);

  entry.querySelector('.emoji-trigger').addEventListener('click', (e) => {
    const rect = e.target.getBoundingClientRect();
    picker.style.display = 'block';
    picker.style.left = `${rect.left}px`;
    picker.style.top = `${rect.bottom + 5}px`;
    currentInput = e.target.closest('.input-with-emoji').querySelector('input, textarea');
    currentTrigger = e.target;
  });

  updateSkills();
};

// Mise à jour des compétences
function updateSkills() {
  const skillEntries = Array.from(document.querySelectorAll('.skills-entry')).map(entry => {
    const skillInput = entry.querySelector('input');
    const levelSelect = entry.querySelector('select');
    const emojiTrigger = entry.querySelector('.emoji-trigger');

    if (!skillInput || !levelSelect || !skillInput.value) {
      return null;
    }

    return {
      skill: `${emojiTrigger.textContent} ${skillInput.value}`,
      level: levelSelect.value
    };
  }).filter(skill => skill !== null);

  cvData.skills = skillEntries;
  updatePreview();
}

// Add event listener for skills updates
document.getElementById('skillsContainer').addEventListener('input', () => {
  updateSkills();
});

// Ajout d'une expérience
window.addExperience = () => {
  const container = document.getElementById('experienceContainer');
  const entry = document.createElement('div');
  entry.className = 'experience-entry';
  entry.innerHTML = `
    <div class="input-with-emoji">
      <input type="text" placeholder="Titre du poste">
      <button type="button" class="emoji-trigger" data-target="experience-title" data-default="💼">💼</button>
    </div>
    <div class="input-with-emoji">
      <input type="text" placeholder="Entreprise">
      <button type="button" class="emoji-trigger" data-target="experience-company" data-default="🏢">🏢</button>
    </div>
    <input type="text" placeholder="Localisation">
    <input type="date" placeholder="Date de début">
    <input type="date" placeholder="Date de fin">
    <div class="input-with-emoji">
      <textarea placeholder="Description des responsabilités"></textarea>
      <button type="button" class="emoji-trigger" data-target="experience-description" data-default="📋">📋</button>
    </div>
  `;
  container.insertBefore(entry, container.lastElementChild);

  entry.querySelectorAll('.emoji-trigger').forEach(trigger => {
    trigger.addEventListener('click', (e) => {
      const rect = e.target.getBoundingClientRect();
      picker.style.display = 'block';
      picker.style.left = `${rect.left}px`;
      picker.style.top = `${rect.bottom + 5}px`;
      currentInput = e.target.closest('.input-with-emoji').querySelector('input, textarea');
      currentTrigger = e.target;
    });
  });
};

// Ajout d'une formation
window.addEducation = () => {
  const container = document.getElementById('educationContainer');
  const entry = document.createElement('div');
  entry.className = 'education-entry';
  entry.innerHTML = `
    <div class="input-with-emoji">
      <input type="text" placeholder="Diplôme">
      <button type="button" class="emoji-trigger" data-target="education-degree" data-default="🎓">🎓</button>
    </div>
    <div class="input-with-emoji">
      <input type="text" placeholder="École">
      <button type="button" class="emoji-trigger" data-target="education-school" data-default="🏫">🏫</button>
    </div>
    <input type="text" placeholder="Localisation">
    <input type="date" placeholder="Date de début">
    <input type="date" placeholder="Date de fin">
    <div class="input-with-emoji">
      <textarea placeholder="Description"></textarea>
      <button type="button" class="emoji-trigger" data-target="education-description" data-default="📚">📚</button>
    </div>
  `;
  container.insertBefore(entry, container.lastElementChild);

  entry.querySelectorAll('.emoji-trigger').forEach(trigger => {
    trigger.addEventListener('click', (e) => {
      const rect = e.target.getBoundingClientRect();
      picker.style.display = 'block';
      picker.style.left = `${rect.left}px`;
      picker.style.top = `${rect.bottom + 5}px`;
      currentInput = e.target.closest('.input-with-emoji').querySelector('input, textarea');
      currentTrigger = e.target;
    });
  });
};

// Ajout d'une langue
window.addLanguage = () => {
  const container = document.getElementById('languagesContainer');
  const entry = document.createElement('div');
  entry.className = 'language-entry';
  entry.innerHTML = `
    <div class="input-with-emoji">
      <input type="text" placeholder="Langue">
      <button type="button" class="emoji-trigger" data-target="language" data-default="🗣️">🗣️</button>
    </div>
    <select>
      <option value="debutant">Débutant</option>
      <option value="intermediaire">Intermédiaire</option>
      <option value="avance">Avancé</option>
      <option value="bilingue">Bilingue</option>
    </select>
  `;
  container.insertBefore(entry, container.lastElementChild);

  entry.querySelector('.emoji-trigger').addEventListener('click', (e) => {
    const rect = e.target.getBoundingClientRect();
    picker.style.display = 'block';
    picker.style.left = `${rect.left}px`;
    picker.style.top = `${rect.bottom + 5}px`;
    currentInput = e.target.closest('.input-with-emoji').querySelector('input, textarea');
    currentTrigger = e.target;
  });
};

function updatePreview() {
  const preview = document.getElementById('cvPreview');

  // Collect experience data
  const experienceEntries = Array.from(document.querySelectorAll('.experience-entry')).map(entry => {
    const titleTrigger = entry.querySelector('[data-target="experience-title"]');
    const companyTrigger = entry.querySelector('[data-target="experience-company"]');
    const descTrigger = entry.querySelector('[data-target="experience-description"]');

    return {
      title: `${titleTrigger?.textContent || ''} ${entry.querySelector('input[placeholder="Titre du poste"]')?.value || ''}`,
      company: `${companyTrigger?.textContent || ''} ${entry.querySelector('input[placeholder="Entreprise"]')?.value || ''}`,
      location: entry.querySelector('input[placeholder="Localisation"]')?.value || '',
      startDate: entry.querySelector('input[type="date"]:nth-of-type(1)')?.value || '',
      endDate: entry.querySelector('input[type="date"]:nth-of-type(2)')?.value || '',
      description: `${descTrigger?.textContent || ''} ${entry.querySelector('textarea')?.value || ''}`
    };
  }).filter(exp => exp.title || exp.company);

  // Collect education data
  const educationEntries = Array.from(document.querySelectorAll('.education-entry')).map(entry => {
    const degreeTrigger = entry.querySelector('[data-target="education-degree"]');
    const schoolTrigger = entry.querySelector('[data-target="education-school"]');
    const descTrigger = entry.querySelector('[data-target="education-description"]');

    return {
      degree: `${degreeTrigger?.textContent || ''} ${entry.querySelector('input[placeholder="Diplôme"]')?.value || ''}`,
      school: `${schoolTrigger?.textContent || ''} ${entry.querySelector('input[placeholder="École"]')?.value || ''}`,
      location: entry.querySelector('input[placeholder="Localisation"]')?.value || '',
      startDate: entry.querySelector('input[type="date"]:nth-of-type(1)')?.value || '',
      endDate: entry.querySelector('input[type="date"]:nth-of-type(2)')?.value || '',
      description: `${descTrigger?.textContent || ''} ${entry.querySelector('textarea')?.value || ''}`
    };
  }).filter(edu => edu.degree || edu.school);

  // Collect language data
  const languageEntries = Array.from(document.querySelectorAll('.language-entry')).map(entry => {
    const languageInput = entry.querySelector('input');
    const levelSelect = entry.querySelector('select');
    const emojiTrigger = entry.querySelector('.emoji-trigger');

    if (!languageInput || !levelSelect || !languageInput.value || !levelSelect.value) {
      return null;
    }

    return {
      language: `${emojiTrigger.textContent} ${languageInput.value}`,
      level: levelSelect.value
    };
  }).filter(lang => lang !== null);

  preview.innerHTML = `
    <div class="cv-left-column">
      ${cvData.personalInfo.photo ? `<img src="${cvData.personalInfo.photo}" class="profile-photo" alt="Photo de profil">` : ''}
      <div class="contact-info">
        ${cvData.personalInfo.fullName ? `<p><strong>👤 ${cvData.personalInfo.fullName}</strong></p>` : ''}
        ${cvData.personalInfo.email ? `<p>📧 ${cvData.personalInfo.email}</p>` : ''}
        ${cvData.personalInfo.phone ? `<p>📱 ${cvData.personalInfo.phone}</p>` : ''}
        ${cvData.personalInfo.location ? `<p>📍 ${cvData.personalInfo.location}</p>` : ''}
      </div>

      ${cvData.skills.length > 0 ? `
      <div class="cv-section">
        <h2>⭐ Compétences</h2>
        <div class="skills-list">
          ${cvData.skills.map(skill => `
            <div class="skill-item">
              <span class="skill-name">${skill.skill}</span>
              <span class="skill-level">${skill.level}</span>
            </div>
          `).join('')}
        </div>
      </div>
      ` : ''}

      ${languageEntries.length > 0 ? `
      <div class="cv-section">
        <h2>🗣️ Langues</h2>
        <div class="languages-list">
          ${languageEntries.map(lang => `
            <div class="language-item">
              <span class="language-name">${lang.language}</span>
              <span class="language-level">${lang.level}</span>
            </div>
          `).join('')}
        </div>
      </div>
      ` : ''}
    </div>

    <div class="cv-right-column">
      ${cvData.summary ? `
      <div class="cv-section">
        <h2>📝 Résumé professionnel</h2>
        <p class="summary">${cvData.summary}</p>
      </div>
      ` : ''}

      ${experienceEntries.length > 0 ? `
      <div class="cv-section">
        <h2>💼 Expérience professionnelle</h2>
        ${experienceEntries.map(exp => `
          <div class="experience-item">
            <h3>${exp.title}</h3>
            <p class="company-info">${exp.company}${exp.location ? ` - ${exp.location}` : ''}</p>
            <p class="date-range">${exp.startDate} - ${exp.endDate || 'Présent'}</p>
            <p class="description">${exp.description}</p>
          </div>
        `).join('')}
      </div>
      ` : ''}

      ${educationEntries.length > 0 ? `
      <div class="cv-section">
        <h2>🎓 Formation</h2>
        ${educationEntries.map(edu => `
          <div class="education-item">
            <h3>${edu.degree}</h3>
            <p class="school-info">${edu.school}${edu.location ? ` - ${edu.location}` : ''}</p>
            <p class="date-range">${edu.startDate} - ${edu.endDate || 'Présent'}</p>
            <p class="description">${edu.description}</p>
          </div>
        `).join('')}
      </div>
      ` : ''}
    </div>
  `;

  // Ajoutez ces scripts dans votre fichier HTML
// <script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js"></script>
// <script src="https://cdnjs.cloudflare.com/ajax/libs/html2canvas/1.4.1/html2canvas.min.js"></script>

  window.generatePDF = function() {
    // Créer un message de chargement
    const loadingMessage = document.createElement('div');
    loadingMessage.textContent = 'Génération du PDF en cours...';
    loadingMessage.style.position = 'fixed';
    loadingMessage.style.top = '50%';
    loadingMessage.style.left = '50%';
    loadingMessage.style.transform = 'translate(-50%, -50%)';
    loadingMessage.style.padding = '20px';
    loadingMessage.style.background = 'rgba(0,0,0,0.7)';
    loadingMessage.style.color = 'white';
    loadingMessage.style.borderRadius = '5px';
    loadingMessage.style.zIndex = '9999';
    document.body.appendChild(loadingMessage);

    // Référence à l'élément CV
    const element = document.getElementById('cvPreview');

    // Créer une copie du CV pour le PDF
    const clone = element.cloneNode(true);
    clone.style.width = '794px'; // Format A4 en pixels
    clone.style.height = '1123px';
    clone.style.padding = '40px';
    clone.style.backgroundColor = 'white';
    clone.style.position = 'absolute';
    clone.style.left = '-9999px';
    clone.style.top = '0';
    document.body.appendChild(clone);

    // Utiliser html2canvas puis jsPDF
    html2canvas(clone, {
      scale: 2,
      useCORS: true,
      logging: false,
      allowTaint: true
    }).then(canvas => {
      // Créer le PDF
      const imgData = canvas.toDataURL('image/jpeg', 1.0);
      const pdf = new jspdf.jsPDF('p', 'mm', 'a4');

      const pdfWidth = pdf.internal.pageSize.getWidth();
      const pdfHeight = pdf.internal.pageSize.getHeight();
      const imgWidth = canvas.width;
      const imgHeight = canvas.height;
      const ratio = Math.min(pdfWidth / imgWidth, pdfHeight / imgHeight);
      const imgX = (pdfWidth - imgWidth * ratio) / 2;
      const imgY = 0;

      pdf.addImage(imgData, 'JPEG', imgX, imgY, imgWidth * ratio, imgHeight * ratio);
      pdf.save('mon-cv.pdf');

      // Nettoyage
      document.body.removeChild(clone);
      document.body.removeChild(loadingMessage);
    }).catch(error => {
      console.error('Erreur lors de la génération du PDF:', error);

      // Nettoyage en cas d'erreur
      if (document.body.contains(clone)) {
        document.body.removeChild(clone);
      }
      document.body.removeChild(loadingMessage);

      alert("Une erreur est survenue lors de la génération du PDF. Veuillez réessayer.");
    });
  };
// Export DOCX
  window.generateDOCX = () => {
    const content = document.getElementById('cvPreview').innerHTML;
    const turndownService = new TurndownService();
    const markdown = turndownService.turndown(content);

    const blob = new Blob([markdown], {type: 'text/markdown'});
    const url = window.URL.createObjectURL(blob);

    const a = document.createElement('a');
    a.href = url;
    a.download = 'mon-cv.doc';
    a.click();

    window.URL.revokeObjectURL(url);
  };

// Initialisation
  updatePreview();
}