async function upvoteClickHandler(event) {
//prevents default action
  event.preventDefault();

//extract id from url
  const id = window.location.toString().split('/')[
    window.location.toString().split('/').length - 1
  ];

//fetch upvote and using put method to update; accessing data from body, stringifying postId: variable id
  const response = await fetch('/posts/upvote', {
    method: 'PUT',
    body: JSON.stringify({
        postId: id
    }),
    headers: {
      'Content-Type': 'application/json'
    }
  });

//reloads page with updated vote count if res is ok
  if (response.ok) {
    document.location.reload();
  } else {
    alert(response.statusText);
  }
}

document.querySelector('.upvote-btn').addEventListener('click', upvoteClickHandler);