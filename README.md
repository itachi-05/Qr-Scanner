# Qr-Scanner

An application through which you can easily generate and scan Qr Code for anyone and share it with ease.

<h2>Screenshots</h2>

![github_qr_readme](https://user-images.githubusercontent.com/83690778/226831883-64a65de7-1872-4c1f-b067-975213055d9d.jpg)

<h2>Features</h2>
<ul>
  <li>Users can generate Qr Code for any kind of text whether it's a simple text or hyperlink</li>
  <li>User can scan any Qr Code and get the details of the Qr Code and can redirect to web links depending on the details</li>
  <li>Recent Qr Codes are stored in Room db once they are generated or scanned</li>
  <li>Offline storing Qr Codes is also available. Users can save the qr code in their device in Downloads folder</li>
</ul>

This application uses:
<ul>
  <li>Room Database</li>
  <li>Viewpage2 and TabLayout</li>
  <li>ViewModels to run SQLite database tasks on background thread using coroutines</li>
  <li>Recycler Views</li>
  <li>Using LiveData to observe changes from database in realtime</li>
</ul>
