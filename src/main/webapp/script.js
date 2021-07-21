// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.


/** Loads the posts from the server and adds it to the DOM*/
function loadPosts(){
    fetch('/list-posts').then(response => response.json()).then((posts) => {
        const postListElement = document.getElementById('post-list');
        posts.forEach((post) => {    
            postListElement.appendChild(createPosts(post));
        })
    }); 
}


/** Creates the posts that match the users entries*/

function createPosts(post){
    //TODO: Find out a way to compare the distance from the zip code the user entered
    //      to the posts in the data storage
     
    const postElement = document.createElement('li');
    postElement.className = "post";

    const formattedPostElement = document.createElement('span');
    formattedPostElement.innerText = format(post);

    postElement.appendChild(formattedPostElement);
    return postElement;
}

/** Formats the post */

function format(post){
    const newPost = post.locationName + "\n Overall Rating: " + post.ratingScore + " \n Parking: " + post.parking + "\n Noise Level (1 is quiet, 5 is loud): " + post.noiseScore + "\n Space Rating (1 is cramped, 5 is roomy): " + post.spaceScore;
    console.log(newPost);

    return newPost;
}
