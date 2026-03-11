from pdfplumber import open as pdf_open
from docx import Document

pdf_path = "./3_计算机科学与技术-袁嘉程-论文-张瀚驰.pdf"
docx_path = "./3_计算机科学与技术-袁嘉程-论文-张瀚驰.docx"

# 打开 PDF
doc = Document()
with pdf_open(pdf_path) as pdf:
    for i, page in enumerate(pdf.pages):
        text = page.extract_text()
        if text:
            doc.add_paragraph(text)
        doc.add_page_break()  # 每页添加分页

# 保存 Word 文件
doc.save(docx_path)
docx_path