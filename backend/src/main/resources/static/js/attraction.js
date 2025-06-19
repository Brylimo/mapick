document.getElementById("searchBtn").addEventListener("click", function (event) {
    event.preventDefault();

    let region = document.querySelector("select[name='region']").value;
    let subregion = document.querySelector("select[name='subregion']").value;
    let category = document.querySelector("select[name='category']").value;

    fetch(root + `/attractions` + `?region=` + region + `&subregion=` + subregion + `&category=` + category)
        .then(response => response.json())
        .then(data => {
            if (data.data && data.data.length > 0) {
                const mappedData = [];
                data.data.forEach(item => {
					console.log("DEBUG:", item.title, item.mapx, item.mapy);
                    item.latlng = {x:item.mapx, y: item.mapy};
                    mappedData.push(item);
                });
                KakaoMap.updateMap(mappedData, "attraction");

            } else {
                alert("검색 결과가 없습니다.");
            }
        })
        .catch(error => {
            console.error("검색 오류:", error);
            document.getElementById("resultContainer").innerHTML = "<p class='text-danger'>검색 중 오류가 발생했습니다.</p>";
        });
});